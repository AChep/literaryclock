import groovy.json.JsonSlurper
import java.awt.Font as AwtFont
import java.awt.font.FontRenderContext
import java.awt.font.LineBreakMeasurer
import java.awt.font.TextAttribute
import java.io.FileInputStream
import java.text.AttributedString
import java.util.*
import kotlin.math.ceil
import kotlin.math.pow
import kotlin.math.roundToInt

plugins {
    id("com.android.application")
}

private val watchFaceWidth = 450
private val watchFaceHeight = 450
private val focusTextHorizontalPadding = 20
private val focusTextX = focusTextHorizontalPadding
private val focusTextWidth = watchFaceWidth - focusTextHorizontalPadding * 2
private val focusFontSize = 40
private val interactiveQuoteTextColor = "#f4f1ea"
private val quoteAccentColorConfigurationId = "quoteAccentColor"
private val interactiveQuoteTimeColor = "[CONFIGURATION.$quoteAccentColorConfigurationId]"
private val ambientQuoteTextColor = "#5f6368"
private val ambientQuoteTimeColor = "#ffffff"
private val strongOpen = "<strong>"
private val strongClose = "</strong>"

private data class RawQuote(
    val index: Int,
    val time: Int,
    val quote: String,
)

private data class TextRun(
    val text: String,
    val emphasized: Boolean,
)

private data class SelectedQuote(
    val time: Int,
    val quoteRuns: List<TextRun>,
)

private data class FocusQuoteLayout(
    val quoteY: Int,
    val quoteHeight: Int,
    val quoteFontSize: Int,
    val quoteMaxLines: Int,
)

private data class QuoteColors(
    val text: String,
    val time: String,
)

private data class MeasuredTextMetrics(
    val totalLines: Int,
    val totalHeight: Int,
    val emphasizedStartY: Float?,
    val emphasizedEndY: Float?,
)

private data class EmphasizedRange(
    val start: Int,
    val end: Int,
)

private data class MeasuredText(
    val text: String,
    val emphasizedRanges: List<EmphasizedRange>,
)

private data class AccentColorOption(
    val id: String,
    val displayName: String,
    val color: String,
)

private val quoteAccentColorOptions = listOf(
    AccentColorOption(id = "blue", displayName = "quote_accent_color_blue", color = "#ff5891ee"),
    AccentColorOption(id = "cyan", displayName = "quote_accent_color_cyan", color = "#ff00bcd4"),
    AccentColorOption(id = "green", displayName = "quote_accent_color_green", color = "#ff4caf50"),
    AccentColorOption(id = "amber", displayName = "quote_accent_color_amber", color = "#ffffb300"),
    AccentColorOption(id = "pink", displayName = "quote_accent_color_pink", color = "#ffec407a"),
    AccentColorOption(id = "purple", displayName = "quote_accent_color_purple", color = "#ff9575cd"),
)

val keystoreProperties = Properties()
val keystorePropertiesFile = rootProject.file("app/literaryclock-release.properties")
if (keystorePropertiesFile.exists()) {
    var stream: FileInputStream? = null
    try {
        stream = keystorePropertiesFile.inputStream()
        keystoreProperties.load(stream)
    } finally {
        stream?.close()
    }
}

android {
    namespace = "com.artemchep.literaryclock"
    compileSdk = Android.targetSdkVersion

    defaultConfig {
        applicationId = "com.artemchep.literaryclock"
        minSdk = 35
        targetSdk = Android.targetSdkVersion

        val versionNamePartsCount = 4
        val releaseTag = System.getenv("LITERARY_CLOCK_RELEASE_TAG")
            ?.takeIf { it.isNotEmpty() }
            ?: "0.1.0-0"
        val releaseTagRegex = Regex("[^0-9]+")
        val versionParts = releaseTag
            .split(releaseTagRegex)
            .mapNotNull { it.toIntOrNull() }
            .run {
                // make sure the list is at least N digit long
                this + List(versionNamePartsCount) { 0 }
            }
            .take(versionNamePartsCount)
        versionCode = versionParts
            .mapIndexed { index, v ->
                val reverseIndex = versionParts.size - index - 1
                v * 100.toDouble().pow(reverseIndex).toInt()
            }
            .sum() * 10 + 2
        versionName = versionParts.joinToString(separator = ".")

        setProperty("archivesBaseName", "literaryclock-watchface")
    }

    signingConfigs {
        create("release") {
            keyAlias = keystoreProperties.getProperty("key_alias")
            keyPassword = keystoreProperties.getProperty("password_store")
            storeFile = rootProject.file("app/literaryclock-release.keystore")
            storePassword = keystoreProperties.getProperty("password_key")
        }
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }

        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }
}

androidComponents {
    onVariants(selector().all()) { variant ->
        val variantName = variant.name
        val taskSuffix = variantName.replaceFirstChar { it.uppercaseChar() }
        listOf(
            "mergeDex$taskSuffix",
            "mergeProjectDex$taskSuffix",
        ).forEach { mergeDexTaskName ->
            tasks.matching { it.name == mergeDexTaskName }.configureEach {
                outputs.upToDateWhen { false }
                doLast {
                    val dexDir = layout.buildDirectory
                        .dir("intermediates/dex/$variantName/$mergeDexTaskName")
                        .get()
                        .asFile
                    if (dexDir.exists()) {
                        dexDir
                            .walkTopDown()
                            .filter { it.isFile && it.extension == "dex" }
                            .forEach { dexFile ->
                                if (!dexFile.delete()) {
                                    throw GradleException("Failed to remove generated dex file: $dexFile")
                                }
                            }
                    }
                }
            }
        }
    }
}

private val sourceDatabase = rootProject.layout.projectDirectory.file("app/src/main/res/raw/database.json")
private val generatedWatchFaceXml = layout.projectDirectory.file("src/main/res/raw/watchface.xml")
private val focusFont = layout.projectDirectory.file("src/main/res/font/robotoslab.ttf")

val generateLiteraryWatchFaceXml by tasks.registering {
    group = "watch face"
    description = "Regenerates the Literary Clock Watch Face Format XML from the app quote database."

    inputs.file(sourceDatabase)
    inputs.file(focusFont)
    outputs.file(generatedWatchFaceXml)

    doLast {
        val output = generatedWatchFaceXml.asFile
        output.parentFile.mkdirs()
        output.writeText(generateWatchFaceXml(sourceDatabase.asFile, focusFont.asFile))
    }
}

val verifyLiteraryWatchFaceXml by tasks.registering {
    group = "verification"
    description = "Verifies that the checked-in Literary Clock Watch Face Format XML is current."

    mustRunAfter(generateLiteraryWatchFaceXml)

    inputs.file(sourceDatabase)
    inputs.file(focusFont)
    inputs.file(generatedWatchFaceXml)

    doLast {
        val expected = generateWatchFaceXml(sourceDatabase.asFile, focusFont.asFile)
        val output = generatedWatchFaceXml.asFile
        if (!output.exists()) {
            throw GradleException(
                "Missing ${output.relativeTo(projectDir)}. Run :watchface:generateLiteraryWatchFaceXml.",
            )
        }

        val actual = output.readText()
        if (actual != expected) {
            throw GradleException(
                "${output.relativeTo(projectDir)} is stale. Run :watchface:generateLiteraryWatchFaceXml.",
            )
        }
    }
}

tasks.named("preBuild") {
    dependsOn(generateLiteraryWatchFaceXml)
}

private fun generateWatchFaceXml(databaseFile: java.io.File, fontFile: java.io.File): String {
    val quotes = readQuotes(databaseFile)
    val selectedQuotes = selectQuotes(quotes)
    val fontFamily = AwtFont
        .createFont(AwtFont.TRUETYPE_FONT, fontFile)
        .deriveFont(focusFontSize.toFloat())

    return buildString {
        appendLine("""<?xml version="1.0" encoding="utf-8"?>""")
        appendLine("""<WatchFace width="$watchFaceWidth" height="$watchFaceHeight" clipShape="CIRCLE">""")
        appendUserConfigurations(indent = "    ")
        appendLine("    <Scene>")
        appendLine("""        <PartDraw x="0" y="0" width="$watchFaceWidth" height="$watchFaceHeight" name="background">""")
        appendLine("""            <Rectangle x="0" y="0" width="$watchFaceWidth" height="$watchFaceHeight">""")
        appendLine("""                <Fill color="#000000" />""")
        appendLine("            </Rectangle>")
        appendLine("        </PartDraw>")
        appendQuoteCondition(
            selectedQuotes = selectedQuotes,
            fontFamily = fontFamily,
            indent = "        ",
        )
        appendLine("    </Scene>")
        appendLine("</WatchFace>")
    }
}

private fun StringBuilder.appendUserConfigurations(indent: String) {
    appendLine("${indent}<UserConfigurations>")
    appendLine(
        """$indent    <ColorConfiguration id="$quoteAccentColorConfigurationId" displayName="quote_accent_color" defaultValue="${quoteAccentColorOptions.first().id}">""",
    )
    for (option in quoteAccentColorOptions) {
        appendLine(
            """$indent        <ColorOption id="${option.id}" displayName="${option.displayName}" colors="${option.color}" />""",
        )
    }
    appendLine("$indent    </ColorConfiguration>")
    appendLine("${indent}</UserConfigurations>")
}

private fun StringBuilder.appendQuoteCondition(
    selectedQuotes: Map<Int, SelectedQuote>,
    fontFamily: AwtFont,
    indent: String,
) {
    appendLine("""${indent}<Group x="0" y="0" width="$watchFaceWidth" height="$watchFaceHeight" name="focus_quotes_interactive">""")
    appendLine("""$indent    <Variant mode="AMBIENT" target="alpha" value="0" />""")
    appendQuoteConditionContent(
        selectedQuotes = selectedQuotes,
        fontFamily = fontFamily,
        colors = QuoteColors(
            text = interactiveQuoteTextColor,
            time = interactiveQuoteTimeColor,
        ),
        indent = "$indent    ",
    )
    appendLine("$indent</Group>")
    appendLine("""${indent}<Group x="0" y="0" width="$watchFaceWidth" height="$watchFaceHeight" name="focus_quotes_ambient" alpha="0">""")
    appendLine("""$indent    <Variant mode="AMBIENT" target="alpha" value="255" />""")
    appendQuoteConditionContent(
        selectedQuotes = selectedQuotes,
        fontFamily = fontFamily,
        colors = QuoteColors(
            text = ambientQuoteTextColor,
            time = ambientQuoteTimeColor,
        ),
        indent = "$indent    ",
        nameSuffix = "_ambient",
    )
    appendLine("$indent</Group>")
}

private fun StringBuilder.appendQuoteConditionContent(
    selectedQuotes: Map<Int, SelectedQuote>,
    fontFamily: AwtFont,
    colors: QuoteColors,
    indent: String,
    nameSuffix: String = "",
) {
    appendLine("${indent}<Condition>")
    appendLine("$indent    <Expressions>")
    for (hour in 0..23) {
        appendLine("""$indent        <Expression name="hour_${hour.twoDigits()}"><![CDATA[[HOUR_0_23] == $hour]]></Expression>""")
    }
    appendLine("$indent    </Expressions>")
    for (hour in 0..23) {
        appendLine("""$indent    <Compare expression="hour_${hour.twoDigits()}">""")
        appendHourCondition(
            hour = hour,
            selectedQuotes = selectedQuotes,
            fontFamily = fontFamily,
            colors = colors,
            indent = "$indent        ",
            nameSuffix = nameSuffix,
        )
        appendLine("$indent    </Compare>")
    }
    appendLine("$indent    <Default>")
    appendFocusQuoteGroup(
        quote = selectedQuotes.getValue(0),
        fontFamily = fontFamily,
        colors = colors,
        indent = "$indent        ",
        nameSuffix = "default$nameSuffix",
        groupName = "focus_quote_default$nameSuffix",
    )
    appendLine("$indent    </Default>")
    appendLine("${indent}</Condition>")
}

private fun StringBuilder.appendHourCondition(
    hour: Int,
    selectedQuotes: Map<Int, SelectedQuote>,
    fontFamily: AwtFont,
    colors: QuoteColors,
    indent: String,
    nameSuffix: String,
) {
    appendLine("${indent}<Condition>")
    appendLine("$indent    <Expressions>")
    for (minute in 0..59) {
        appendLine("""$indent        <Expression name="minute_${minute.twoDigits()}"><![CDATA[[MINUTE] == $minute]]></Expression>""")
    }
    appendLine("$indent    </Expressions>")
    for (minute in 0..59) {
        val time = hour * 60 + minute
        appendLine("""$indent    <Compare expression="minute_${minute.twoDigits()}">""")
        appendFocusQuoteGroup(
            quote = selectedQuotes.getValue(time),
            fontFamily = fontFamily,
            colors = colors,
            indent = "$indent        ",
            nameSuffix = "${time.fourDigits()}$nameSuffix",
            groupName = "focus_quote_minute_${time.fourDigits()}$nameSuffix",
        )
        appendLine("$indent    </Compare>")
    }
    appendLine("$indent    <Default>")
    appendFocusQuoteGroup(
        quote = selectedQuotes.getValue(hour * 60),
        fontFamily = fontFamily,
        colors = colors,
        indent = "$indent        ",
        nameSuffix = "default_hour_${hour.twoDigits()}$nameSuffix",
        groupName = "focus_quote_default_hour_${hour.twoDigits()}$nameSuffix",
    )
    appendLine("$indent    </Default>")
    appendLine("${indent}</Condition>")
}

private fun StringBuilder.appendFocusQuoteGroup(
    quote: SelectedQuote,
    fontFamily: AwtFont,
    colors: QuoteColors,
    indent: String,
    nameSuffix: String = quote.time.fourDigits(),
    groupName: String = "focus_quote_minute_${quote.time.fourDigits()}",
) {
    val layout = quote.focusLayout(fontFamily)
    appendLine("""${indent}<Group x="0" y="0" width="$watchFaceWidth" height="$watchFaceHeight" name="$groupName">""")
    appendLine("""$indent    <PartText x="$focusTextX" y="${layout.quoteY}" width="$focusTextWidth" height="${layout.quoteHeight}" name="focus_quote_text_$nameSuffix">""")
    appendLine("""$indent        <Text align="START" maxLines="${layout.quoteMaxLines}" isAutoSize="FALSE" ellipsis="FALSE">""")
    for (run in quote.quoteRuns) {
        appendTextFont(
            indent = "$indent            ",
            text = run.text,
            color = if (run.emphasized) colors.time else colors.text,
            size = layout.quoteFontSize.toString(),
            weight = if (run.emphasized) "BOLD" else null,
        )
    }
    appendLine("$indent        </Text>")
    appendLine("$indent    </PartText>")
    appendLine("$indent</Group>")
}

private fun SelectedQuote.focusLayout(fontFamily: AwtFont): FocusQuoteLayout {
    val metrics = quoteRuns.measureTextLayout(fontFamily, width = focusTextWidth)
    val emphasizedCenterY = if (
        metrics.emphasizedStartY != null &&
        metrics.emphasizedEndY != null
    ) {
        (metrics.emphasizedStartY + metrics.emphasizedEndY) / 2.0
    } else {
        metrics.totalHeight / 2.0
    }
    val quoteY = ((watchFaceHeight / 2.0) - emphasizedCenterY).roundToInt()

    return FocusQuoteLayout(
        quoteY = quoteY,
        quoteHeight = metrics.totalHeight,
        quoteFontSize = focusFontSize,
        quoteMaxLines = metrics.totalLines,
    )
}

private fun List<TextRun>.measureTextLayout(fontFamily: AwtFont, width: Int): MeasuredTextMetrics {
    val measuredText = toMeasuredText()
    if (measuredText.text.isEmpty()) {
        return MeasuredTextMetrics(
            totalLines = 1,
            totalHeight = focusFontSize,
            emphasizedStartY = null,
            emphasizedEndY = null,
        )
    }

    val regularFont = fontFamily.deriveFont(AwtFont.PLAIN, focusFontSize.toFloat())
    val boldFont = fontFamily.deriveFont(AwtFont.BOLD, focusFontSize.toFloat())
    val attributedString = AttributedString(measuredText.text).apply {
        addAttribute(TextAttribute.FONT, regularFont)
        for (range in measuredText.emphasizedRanges) {
            addAttribute(TextAttribute.FONT, boldFont, range.start, range.end)
        }
    }
    val iterator = attributedString.iterator
    val lineBreakMeasurer = LineBreakMeasurer(
        iterator,
        FontRenderContext(null, true, true),
    )

    var totalLines = 0
    var currentY = 0f
    var emphasizedStartY: Float? = null
    var emphasizedEndY: Float? = null

    while (lineBreakMeasurer.position < iterator.endIndex) {
        val lineStart = lineBreakMeasurer.position
        val layout = lineBreakMeasurer.nextLayout(width.toFloat())
        val lineEnd = lineBreakMeasurer.position
        val lineHeight = layout.ascent + layout.descent + layout.leading
        val lineTop = currentY
        val lineBottom = currentY + lineHeight

        if (measuredText.emphasizedRanges.any { it.start < lineEnd && it.end > lineStart }) {
            if (emphasizedStartY == null) {
                emphasizedStartY = lineTop
            }
            emphasizedEndY = lineBottom
        }

        currentY = lineBottom
        totalLines += 1
    }

    return MeasuredTextMetrics(
        totalLines = totalLines,
        totalHeight = ceil(currentY).toInt().coerceAtLeast(1),
        emphasizedStartY = emphasizedStartY,
        emphasizedEndY = emphasizedEndY,
    )
}

private fun List<TextRun>.toMeasuredText(): MeasuredText {
    val text = StringBuilder()
    val emphasizedRanges = mutableListOf<EmphasizedRange>()
    for (run in this) {
        val start = text.length
        text.append(run.text)
        val end = text.length
        if (run.emphasized && start < end) {
            emphasizedRanges += EmphasizedRange(start, end)
        }
    }

    return MeasuredText(
        text = text.toString(),
        emphasizedRanges = emphasizedRanges,
    )
}

private fun StringBuilder.appendTextFont(
    indent: String,
    text: String,
    color: String,
    size: String,
    weight: String? = null,
    slant: String? = null,
) {
    if (text.isEmpty()) return

    append(indent)
    append("""<Font family="robotoslab" size="$size" color="$color"""")
    weight?.let { append(""" weight="$it"""") }
    slant?.let { append(""" slant="$it"""") }
    append(">")
    append(text.xmlEscaped())
    appendLine("</Font>")
}

private fun readQuotes(databaseFile: java.io.File): List<RawQuote> {
    @Suppress("UNCHECKED_CAST")
    val rows = JsonSlurper().parse(databaseFile) as List<Map<String, Any?>>
    return rows.mapIndexed { index, row ->
        RawQuote(
            index = index,
            time = row.intValue("time"),
            quote = row.stringValue("quote"),
        )
    }
}

private fun selectQuotes(quotes: List<RawQuote>): Map<Int, SelectedQuote> {
    val selectedQuotes = quotes
        .groupBy(RawQuote::time)
        .mapValues { (_, candidates) ->
            candidates.minWith(
                compareBy<RawQuote> { it.quote.readableLength() }
                    .thenBy { it.index },
            )
        }

    val expectedMinutes = (0 until 24 * 60).toSet()
    val actualMinutes = selectedQuotes.keys
    val missingMinutes = expectedMinutes - actualMinutes
    val extraMinutes = actualMinutes - expectedMinutes
    if (missingMinutes.isNotEmpty() || extraMinutes.isNotEmpty()) {
        throw GradleException(
            "Quote database must contain exactly one populated group for each minute. " +
                "Missing: ${missingMinutes.sorted()}. Extra: ${extraMinutes.sorted()}.",
        )
    }

    return selectedQuotes.mapValues { (_, quote) ->
        SelectedQuote(
            time = quote.time,
            quoteRuns = quote.quote.toTextRuns(),
        )
    }
}

private fun String.toTextRuns(): List<TextRun> {
    val compact = normalizedWhitespace()
    val openCount = compact.countOccurrences(strongOpen)
    val closeCount = compact.countOccurrences(strongClose)
    if (openCount == 0 || openCount != closeCount) {
        return listOf(TextRun(compact.stripStrongTags(), emphasized = false))
    }

    val regex = Regex("${Regex.escape(strongOpen)}(.*?)${Regex.escape(strongClose)}")
    val runs = mutableListOf<TextRun>()
    var index = 0
    regex.findAll(compact).forEach { match ->
        if (match.range.first > index) {
            runs += TextRun(compact.substring(index, match.range.first), emphasized = false)
        }
        runs += TextRun(match.groupValues[1], emphasized = true)
        index = match.range.last + 1
    }
    if (index < compact.length) {
        runs += TextRun(compact.substring(index), emphasized = false)
    }

    if (runs.any { strongOpen in it.text || strongClose in it.text }) {
        return listOf(TextRun(compact.stripStrongTags(), emphasized = false))
    }

    return runs.filter { it.text.isNotEmpty() }
}

private fun String.readableLength(): Int = stripStrongTags()
    .normalizedWhitespace()
    .length

private fun String.normalizedWhitespace(): String = replace(Regex("\\s+"), " ").trim()

private fun String.stripStrongTags(): String = replace(strongOpen, "")
    .replace(strongClose, "")
    .normalizedWhitespace()

private fun String.countOccurrences(needle: String): Int {
    var count = 0
    var startIndex = 0
    while (true) {
        val index = indexOf(needle, startIndex)
        if (index < 0) return count
        count++
        startIndex = index + needle.length
    }
}

private fun String.xmlEscaped(): String = buildString {
    for (char in this@xmlEscaped) {
        when (char) {
            '&' -> append("&amp;")
            '<' -> append("&lt;")
            '>' -> append("&gt;")
            '"' -> append("&quot;")
            '\'' -> append("&apos;")
            else -> append(char)
        }
    }
}

private fun Map<String, Any?>.stringValue(key: String): String = this[key]?.toString().orEmpty()

private fun Map<String, Any?>.intValue(key: String): Int = when (val value = this[key]) {
    is Number -> value.toInt()
    is String -> value.toInt()
    else -> throw GradleException("Expected integer value for '$key', got: $value")
}

private fun Int.twoDigits(): String = toString().padStart(2, '0')

private fun Int.fourDigits(): String = toString().padStart(4, '0')

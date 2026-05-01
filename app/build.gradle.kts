import com.android.build.api.dsl.ManagedVirtualDevice
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.gradle.testing.jacoco.tasks.JacocoReport
import java.io.FileInputStream
import java.util.*
import kotlin.math.pow

plugins {
    id("com.android.application")
    id("jacoco")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
}

val appDependencies = createDependencies(Module.APP)

val keystoreProperties = Properties()
val keystorePropertiesFile = file("literaryclock-release.properties")
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
    compileSdk = Android.targetSdkVersion
    namespace = "com.artemchep.literaryclock"

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    defaultConfig {
        minSdk = Android.minSdkVersion
        targetSdk = Android.targetSdkVersion
        applicationId = "com.artemchep.literaryclock"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["clearPackageData"] = "true"

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
            .sum() * 10 + 1
        versionName = versionParts.joinToString(separator = ".")

        setProperty("archivesBaseName", "literaryclock")
    }

    signingConfigs {
        create("release") {
            keyAlias = keystoreProperties.getProperty("key_alias")
            keyPassword = keystoreProperties.getProperty("password_store")
            storeFile = file("literaryclock-release.keystore")
            storePassword = keystoreProperties.getProperty("password_key")
        }
    }

    val acraUri = System.getenv("ACRA_URI").orEmpty()
    val acraUsername = System.getenv("ACRA_USERNAME").orEmpty()
    val acraPassword = System.getenv("ACRA_PASSWORD").orEmpty()

    buildTypes {
        getByName("release").apply {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = false // Crashes when using a MotionLayout

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
                "proguard-kodein.pro"
            )
        }

        getByName("debug").apply {
            isMinifyEnabled = false
        }

        // Convert dependencies to java code, to
        // show them later in the app.
        val (bcFieldType, bcFieldValue) = appDependencies.toJavaField()
        val licenseKeyValue = """"${keystoreProperties.getProperty("license_key") ?: "debug"}""""
        forEach { buildType ->
            buildType.buildConfigField(bcFieldType, "DEPENDENCIES", bcFieldValue)
            buildType.buildConfigField("String", "LICENSE_KEY", licenseKeyValue)
            buildType.buildConfigField("String", "ACRA_URI", """"$acraUri"""")
            buildType.buildConfigField("String", "ACRA_USERNAME", """"$acraUsername"""")
            buildType.buildConfigField("String", "ACRA_PASSWORD", """"$acraPassword"""")
        }
    }

    flavorDimensions.add("common")

    productFlavors {
        maybeCreate("prod").apply {
            dimension = "common"
        }
    }

    testOptions {
        animationsDisabled = true
        execution = "ANDROIDX_TEST_ORCHESTRATOR"

        unitTests {
            isIncludeAndroidResources = true
        }

        managedDevices {
            allDevices {
                create<ManagedVirtualDevice>("pixel6Api34") {
                    device = "Pixel 6"
                    apiLevel = 34
                    systemImageSource = "aosp-atd"
                }
            }
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:$GOOGLE_FIREBASE_BOM_VERSION"))
    handle(this, appDependencies)
}

val jacocoExclusions = listOf(
    "**/R.class",
    "**/R$*.class",
    "**/BuildConfig.*",
    "**/Manifest*.*",
    "**/*Binding.class",
    "**/*BindingImpl.class",
    "**/BR.class",
    "**/databinding/**/*.*",
    "**/android/databinding/**/*.*",
    "**/*Directions*.*",
    "**/*Args*.*",
)

tasks.register<JacocoReport>("jacocoProdDebugUnitTestReport") {
    dependsOn("testProdDebugUnitTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    classDirectories.setFrom(
        files(
            fileTree("${layout.buildDirectory.asFile.get()}/tmp/kotlin-classes/prodDebug") {
                exclude(jacocoExclusions)
            },
            fileTree("${layout.buildDirectory.asFile.get()}/intermediates/javac/prodDebug/classes") {
                exclude(jacocoExclusions)
            },
        ),
    )
    sourceDirectories.setFrom(
        files(
            "src/main/java",
            "src/main/kotlin",
        ),
    )
    executionData.setFrom(
        fileTree(layout.buildDirectory.asFile.get()) {
            include(
                "jacoco/testProdDebugUnitTest.exec",
                "outputs/unit_test_code_coverage/prodDebugUnitTest/testProdDebugUnitTest.exec",
            )
        },
    )
}

tasks.register("verifyUnitTests") {
    group = "verification"
    description = "Runs the prodDebug unit test suite and generates the JaCoCo report."
    dependsOn(
        "testProdDebugUnitTest",
        "jacocoProdDebugUnitTestReport",
    )
}

val hasGoogleServicesConfig = fileTree(projectDir) {
    include("google-services.json")
    include("src/**/google-services.json")
}.files.isNotEmpty()

if (hasGoogleServicesConfig) {
    apply(plugin = "com.google.gms.google-services")
}

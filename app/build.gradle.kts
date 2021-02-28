import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.FileInputStream
import java.util.*
import kotlin.math.pow

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("realm-android")
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
    compileSdkVersion(Android.targetSdkVersion)

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        minSdkVersion(Android.minSdkVersion)
        targetSdkVersion(Android.targetSdkVersion)

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
            .sum()
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
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
                "proguard-realm.pro"
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

    flavorDimensions("common")

    productFlavors {
        maybeCreate("prod").apply {
            dimension = "common"
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
    kotlinOptions.freeCompilerArgs = listOf(
        "-Xinline-classes"
    )
}

realm {
    isKotlinExtensionsEnabled = true
}

dependencies {
    handle(this, appDependencies)
}

apply(plugin = "com.google.gms.google-services")

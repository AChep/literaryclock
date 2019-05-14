import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.internal.AndroidExtensionsExtension
import java.io.FileInputStream
import java.util.*

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("realm-android")
}

val appVersionName = "0.2.1"
val appVersionCode = 6
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

    defaultConfig {
        minSdkVersion(Android.minSdkVersion)
        targetSdkVersion(Android.targetSdkVersion)

        versionCode = appVersionCode
        versionName = appVersionName

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

    buildTypes {
        getByName("release").apply {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            isShrinkResources = false
            isUseProguard = false

            proguardFiles(
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
        }
    }

    flavorDimensions("common")

    productFlavors {
        maybeCreate("prod").apply {
            setDimension("common")
        }
    }
}

androidExtensions {
    // See the issue at:
    // https://github.com/gradle/kotlin-dsl/issues/644
    configure(delegateClosureOf<AndroidExtensionsExtension> {
        isExperimental = true
    })
}

realm {
    isKotlinExtensionsEnabled = true
}

dependencies {
    handle(this, appDependencies)
}

apply(plugin = "com.google.gms.google-services")

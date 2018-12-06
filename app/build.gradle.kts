import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.internal.AndroidExtensionsExtension

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs")
    id("realm-android")
}

val appVersionName = "0.0.1"
val appDependencies = createDependencies(Module.APP)

android {
    compileSdkVersion(Android.targetSdkVersion)

    defaultConfig {
        minSdkVersion(Android.minSdkVersion)
        targetSdkVersion(Android.targetSdkVersion)

        versionCode = 1
        versionName = appVersionName

        setProperty("archivesBaseName", "literaryclock")
    }

    buildTypes {
        maybeCreate("release").apply {
            isMinifyEnabled = true
            proguardFiles("proguard-rules.pro")
        }

        maybeCreate("debug").apply {
            isMinifyEnabled = false
        }

        // Convert dependencies to java code, to
        // show them later in the app.
        val (bcFieldType, bcFieldValue) = appDependencies.toJavaField()
        forEach { buildType ->
            buildType.buildConfigField(bcFieldType, "DEPENDENCIES", bcFieldValue)
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
buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:8.13.2")
        classpath("com.google.gms:google-services:4.4.4")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$ANDROIDARCH_NAVIGATION_VERSION")
        classpath(kotlin("gradle-plugin", version = KOTLIN_VERSION))

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

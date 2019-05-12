buildscript {
    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:3.5.0-beta01")
        classpath("io.realm:realm-gradle-plugin:5.8.0")
        classpath("com.google.gms:google-services:4.2.0")
        classpath("android.arch.navigation:navigation-safe-args-gradle-plugin:$ANDROIDARCH_NAVIGATION_VERSION")
        classpath(kotlin("gradle-plugin", version = KOTLIN_VERSION))

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven("https://jitpack.io")
    }
}

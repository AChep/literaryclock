buildscript {
    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:4.0.0-alpha07")
        classpath("io.realm:realm-gradle-plugin:6.0.2")
        classpath("com.google.gms:google-services:4.3.3")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$ANDROIDARCH_NAVIGATION_VERSION")
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

// Root build.gradle.kts
plugins {
    id("com.android.application") version "8.13.2" apply false
    id("com.android.library") version "8.13.2" apply false
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.9.6" apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        // Safe Args plugin classpath (needed for navigation code generation)
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.9.6")
    }
}

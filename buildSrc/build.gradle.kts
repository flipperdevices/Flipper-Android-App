plugins {
    // SEE NOTE_CONFIGURATION_PLUGIN
    id("org.jetbrains.kotlin.jvm") version "1.5.21"
}

repositories {
    mavenCentral()
    google()
    jcenter()
}

dependencies {
    // SEE NOTE_CONFIGURATION_PLUGIN
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.21")
    implementation("com.android.tools.build:gradle:7.0.1")
}

// NOTE_CONFIGURATION_PLUGIN
// when updating this dependency version, also update them in buildSrc/build.gradle.kts
// (manual update is required, because this file is not seen by build scripts in buildSrc)

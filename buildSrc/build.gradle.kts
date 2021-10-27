plugins {
    // Be careful! See more in /buildSrc/src/main/java/Dependencies.kt#NOTE_CONFIGURATION_PLUGIN
    id("org.jetbrains.kotlin.jvm") version "1.5.21"
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    // Be careful! See more in /buildSrc/src/main/java/Dependencies.kt#NOTE_CONFIGURATION_PLUGIN
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31")
    implementation("com.android.tools.build:gradle:7.0.2")
    implementation("com.squareup.anvil:gradle-plugin:2.3.4")
    implementation("com.google.protobuf:protobuf-gradle-plugin:0.8.17")
}

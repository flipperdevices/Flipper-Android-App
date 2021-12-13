plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    // Be careful! See more in /buildSrc/src/main/java/Dependencies.kt#NOTE_CONFIGURATION_PLUGIN
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.0")
    // Be careful! See more in /buildSrc/src/main/java/Dependencies.kt#NOTE_CONFIGURATION_PLUGIN
    implementation("org.jetbrains.kotlin:kotlin-serialization:1.6.0")
    implementation("com.android.tools.build:gradle:7.0.2")
    implementation("com.squareup.anvil:gradle-plugin:2.3.4")
    implementation("com.google.protobuf:protobuf-gradle-plugin:0.8.17")
}

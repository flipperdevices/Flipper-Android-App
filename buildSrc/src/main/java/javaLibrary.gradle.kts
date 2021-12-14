plugins {
    kotlin("jvm")
}

common()

java {
    // force Java 8 source when building java-only artifacts.
    // This is different than the Kotlin jvm target.
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

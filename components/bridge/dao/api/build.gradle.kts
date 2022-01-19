plugins {
    androidLibrary
    id("kotlinx-serialization")
    id("kotlin-parcelize")
}

dependencies {
    implementation(libs.kotlin.coroutines)

    implementation(libs.cicerone)

    implementation(libs.kotlin.serialization.json)
}

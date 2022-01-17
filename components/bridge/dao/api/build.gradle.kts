plugins {
    androidLibrary
    id("kotlinx-serialization")
}

dependencies {
    implementation(libs.kotlin.coroutines)

    implementation(libs.cicerone)

    implementation(libs.kotlin.serialization.json)
}

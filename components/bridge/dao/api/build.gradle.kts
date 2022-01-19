plugins {
    androidLibrary
    id("kotlinx-serialization")
    id("kotlin-parcelize")
}

dependencies {
    implementation(libs.kotlin.coroutines)

    implementation(Libs.CICERONE)

    implementation(libs.kotlin.serialization.json)
}

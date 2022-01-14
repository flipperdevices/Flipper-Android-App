plugins {
    androidLibrary
    id("kotlinx-serialization")
}

dependencies {
    implementation(libs.kotlin.coroutines)

    implementation(Libs.CICERONE)

    implementation(libs.kotlin.serialization.json)
}

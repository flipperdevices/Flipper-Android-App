plugins {
    androidLibrary
    id("kotlin-android")
    id("kotlin-parcelize")
}

dependencies {
    implementation(projects.components.bridge.protobuf)
    implementation(projects.components.core.log)

    implementation(libs.kotlin.coroutines)
    implementation(libs.ble.common)
    implementation(libs.ble.scan)

    implementation(libs.appcompat)
}

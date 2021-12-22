plugins {
    androidLibrary
    id("kotlin-android")
    id("kotlin-parcelize")
}

dependencies {
    implementation(projects.components.bridge.protobuf)

    implementation(libs.kotlin.coroutines)
    implementation(Libs.NORDIC_BLE_COMMON)
    implementation(Libs.NORDIC_BLE_SCAN)
    implementation(Libs.NORDIC_BLE_KTX)

    implementation(Libs.APPCOMPAT)
}

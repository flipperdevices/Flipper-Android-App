plugins {
    androidLibrary
    id("kotlin-android")
    id("kotlin-parcelize")
}

dependencies {
    implementation(project(":components:bridge:protobuf"))

    implementation(Libs.KOTLIN_COROUTINES)
    implementation(Libs.NORDIC_BLE_COMMON)
    implementation(Libs.NORDIC_BLE_SCAN)
    implementation(Libs.NORDIC_BLE_KTX)

    implementation(Libs.APPCOMPAT)
}

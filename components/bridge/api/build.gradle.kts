plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-parcelize")
}
apply<com.flipper.gradle.ConfigurationPlugin>()

dependencies {
    implementation(project(":components:core"))

    implementation(Libs.KOTLIN_COROUTINES)
    implementation(Libs.NORDIC_BLE_COMMON)
    implementation(Libs.NORDIC_BLE_SCAN)
    implementation(Libs.NORDIC_BLE_KTX)

    implementation(Libs.APPCOMPAT)
}

plugins {
    id("com.android.library")
}

apply<com.flipper.gradle.ConfigurationPlugin>()

dependencies {
    implementation(project(":components:core"))

    api(Libs.KOTLIN_COROUTINES)
    implementation(Libs.ANNOTATIONS)
    implementation(Libs.CORE_KTX)
    implementation(Libs.APPCOMPAT)

    implementation(Libs.NORDIC_BLE_SCAN)
    implementation(Libs.NORDIC_BLE)
    implementation(Libs.NORDIC_BLE_KTX)
    implementation(Libs.NORDIC_BLE_COMMON)

    testImplementation(TestingLib.JUNIT)
    androidTestImplementation(TestingLib.ANDROIDX_TEST_EXT_JUNIT)
    androidTestImplementation(TestingLib.ESPRESSO_CORE)
}

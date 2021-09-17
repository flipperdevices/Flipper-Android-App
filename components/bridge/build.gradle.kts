plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
}

apply<com.flipper.gradle.ConfigurationPlugin>()

dependencies {
    implementation(project(":components:core"))

    api(Libs.KOTLIN_COROUTINES)
    implementation(Libs.ANNOTATIONS)
    implementation(Libs.CORE_KTX)
    implementation(Libs.APPCOMPAT)

    implementation(Libs.NORDIC_BLE_SCAN)
    api(Libs.NORDIC_BLE)
    api(Libs.NORDIC_BLE_KTX)
    implementation(Libs.NORDIC_BLE_COMMON)

    implementation(Libs.DAGGER)
    kapt(Libs.DAGGER_COMPILER)

    testImplementation(TestingLib.JUNIT)
    androidTestImplementation(TestingLib.ANDROIDX_TEST_EXT_JUNIT)
    androidTestImplementation(TestingLib.ESPRESSO_CORE)
}

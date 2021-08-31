plugins {
    id("com.android.library")
}

apply<com.flipper.gradle.ConfigurationPlugin>()

dependencies {
    implementation(Libs.TIMBER)
    implementation(Libs.KOTLIN)
    api(Libs.KOTLIN_COROUTINES)
    implementation(Libs.ANNOTATIONS)
    implementation(Libs.CORE_KTX)
    implementation(Libs.APPCOMPAT)

    implementation(Libs.NORDIC_BLE_SCAN)

    testImplementation(TestingLib.JUNIT)
    androidTestImplementation(TestingLib.ANDROIDX_TEST_EXT_JUNIT)
    androidTestImplementation(TestingLib.ESPRESSO_CORE)
}

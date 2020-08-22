plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-android-extensions")
}

apply<com.flipper.gradle.ConfigurationPlugin>()

dependencies {
    implementation(Libs.TIMBER)
    implementation(Libs.KOTLIN)
    implementation(Libs.ANNOTATIONS)
    implementation(Libs.CORE_KTX)
    implementation(Libs.APPCOMPAT)

    testImplementation(TestingLib.JUNIT)
    androidTestImplementation(TestingLib.ANDROIDX_TEST_EXT_JUNIT)
    androidTestImplementation(TestingLib.ESPRESSO_CORE)
}

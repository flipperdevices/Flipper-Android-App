plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
}

apply<com.flipper.gradle.ConfigurationPlugin>()

dependencies {
    implementation(project(":components:core"))
    implementation(project(":components:bridge"))
    implementation(project(":components:pair"))

    implementation(Libs.TIMBER)
    implementation(Libs.KOTLIN)
    implementation(Libs.ANNOTATIONS)
    implementation(Libs.CORE_KTX)
    implementation(Libs.APPCOMPAT)
    implementation(Libs.MATERIAL)
    implementation(Libs.MOXY)
    implementation(Libs.MOXY_KTX)
    implementation(Libs.TREX)
    implementation(Libs.DAGGER)

    testImplementation(TestingLib.JUNIT)
    androidTestImplementation(TestingLib.ANDROIDX_TEST_EXT_JUNIT)
    androidTestImplementation(TestingLib.ESPRESSO_CORE)

    kapt(Libs.MOXY_COMPILER)
    kapt(Libs.DAGGER_COMPILER)
}

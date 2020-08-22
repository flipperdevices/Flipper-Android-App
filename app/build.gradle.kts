plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
}

apply<com.flipper.gradle.ConfigurationPlugin>()

dependencies {
    implementation(project(":core"))
    implementation(project(":bridge"))

    implementation(Libs.TIMBER)
    implementation(Libs.KOTLIN)
    implementation(Libs.ANNOTATIONS)
    implementation(Libs.CORE_KTX)
    implementation(Libs.APPCOMPAT)

    api(Libs.NAVIGATION_FRAGMENT)
    api(Libs.NAVIGATION_UI)
    debugImplementation(Libs.NAVIGATION_FRGMENT_TESTING)
    androidTestImplementation(Libs.NAVIGATION_TESTING)

    api(Libs.CONSTRAINT_LAYOUT)

    testImplementation(TestingLib.JUNIT)
    androidTestImplementation(TestingLib.ANDROIDX_TEST_EXT_JUNIT)
    androidTestImplementation(TestingLib.ESPRESSO_CORE)
}

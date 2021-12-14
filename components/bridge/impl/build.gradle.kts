plugins {
    androidLibrary
    id("com.squareup.anvil")
    kotlin("kapt")
}

dependencies {
    implementation(project(":components:core:log"))
    implementation(project(":components:core:ktx"))

    implementation(project(":components:bridge:api"))
    implementation(project(":components:bridge:protobuf"))

    implementation(Libs.KOTLIN_COROUTINES)
    implementation(Libs.ANNOTATIONS)
    implementation(Libs.CORE_KTX)
    implementation(Libs.APPCOMPAT)

    implementation(Libs.NORDIC_BLE_SCAN)
    implementation(Libs.NORDIC_BLE)
    implementation(Libs.NORDIC_BLE_KTX)
    implementation(Libs.NORDIC_BLE_COMMON)

    implementation(Libs.DAGGER)
    kapt(Libs.DAGGER_COMPILER)

    implementation(Libs.FASTUTIL)

    testImplementation(project(":components:core:test"))
    testImplementation(TestingLib.JUNIT)
    testImplementation(TestingLib.MOCKITO)
    testImplementation(TestingLib.ANDROIDX_TEST_EXT_JUNIT)
    testImplementation(TestingLib.ROBOELECTRIC)
    testImplementation(TestingLib.LIFECYCLE)
    testImplementation(TestingLib.COROUTINES)
}

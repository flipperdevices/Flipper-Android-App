plugins {
    androidLibrary
    id("com.squareup.anvil")
    kotlin("kapt")
}

android {
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

dependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.preference)

    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.provider)
    implementation(projects.components.bridge.protobuf)
    implementation(projects.components.bridge.service.api)
    implementation(projects.components.bridge.impl)

    implementation(Libs.ANNOTATIONS)
    implementation(Libs.APPCOMPAT)

    implementation(libs.kotlin.coroutines)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.service)
    kapt(libs.lifecycle.kapt)

    implementation(libs.ble)
    implementation(libs.ble.ktx)
    implementation(libs.ble.common)
    implementation(libs.ble.scan)

    implementation(libs.dagger)
    kapt(libs.dagger.kapt)

    testImplementation(projects.components.core.test)
    testImplementation(TestingLib.JUNIT)
    testImplementation(TestingLib.MOCKITO)
    testImplementation(TestingLib.ANDROIDX_TEST_EXT_JUNIT)
    testImplementation(TestingLib.ROBOELECTRIC)
    testImplementation(libs.lifecycle.test)
}

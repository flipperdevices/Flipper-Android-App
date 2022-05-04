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
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.log)
    implementation(projects.components.core.preference)

    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.pbutils)
    implementation(projects.components.bridge.service.api)
    implementation(projects.components.bridge.impl)

    implementation(libs.annotations)
    implementation(libs.appcompat)

    implementation(libs.kotlin.coroutines)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.service)
    kapt(libs.lifecycle.kapt)

    implementation(libs.ble)
    implementation(libs.ble.common)
    implementation(libs.ble.scan)

    implementation(libs.dagger)
    kapt(libs.dagger.kapt)

    testImplementation(projects.components.core.test)
    testImplementation(libs.junit)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.ktx.testing)
    testImplementation(libs.roboelectric)
    testImplementation(libs.lifecycle.test)
}

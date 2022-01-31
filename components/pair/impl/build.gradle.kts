plugins {
    androidCompose
    id("com.squareup.anvil")
    kotlin("kapt")
}

android {
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

dependencies {
    implementation(projects.components.core.ui)
    implementation(projects.components.core.di)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.log)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.navigation)

    implementation(projects.components.bottombar.api)

    implementation(projects.components.singleactivity.api)

    implementation(projects.components.deeplink.api)

    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.provider)
    implementation(projects.components.bridge.service.api)
    implementation(projects.components.pair.api)

    implementation(libs.kotlin.coroutines)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.annotations)
    implementation(libs.ktx)
    implementation(libs.appcompat)
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.ktx.fragment)

    // Guide
    implementation(libs.image.slider)
    implementation(libs.image.glide)

    implementation(libs.ble)
    implementation(libs.ble.ktx)
    implementation(libs.ble.common)
    implementation(libs.ble.scan)

    implementation(libs.cicerone)
}

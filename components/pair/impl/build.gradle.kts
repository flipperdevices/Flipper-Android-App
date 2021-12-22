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
    implementation(Libs.LIFECYCLE_RUNTIME_KTX)
    implementation(Libs.LIFECYCLE_VIEWMODEL_KTX)
    implementation(Libs.ANNOTATIONS)
    implementation(Libs.CORE_KTX)
    implementation(Libs.APPCOMPAT)
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(Libs.FRAGMENT_KTX)

    // Guide
    implementation(Libs.IMAGE_SLIDER)
    implementation(Libs.GLIDE)
    implementation(Libs.LOTTIE)

    implementation(Libs.NORDIC_BLE)
    implementation(Libs.NORDIC_BLE_KTX)
    implementation(Libs.NORDIC_BLE_COMMON)
    implementation(Libs.NORDIC_BLE_SCAN)

    implementation(Libs.CICERONE)
}

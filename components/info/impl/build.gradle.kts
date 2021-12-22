plugins {
    androidCompose
    id("com.squareup.anvil")
    kotlin("kapt")
}

dependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.ui)
    implementation(projects.components.core.navigation)

    implementation(projects.components.info.api)
    implementation(projects.components.pair.api)
    implementation(projects.components.screenstreaming.api)
    implementation(projects.components.filemanager.api)
    implementation(projects.components.debug.api)

    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.service.api)
    implementation(projects.components.bridge.protobuf)

    // Core deps
    implementation(Libs.CORE_KTX)
    implementation(Libs.ANNOTATIONS)

    implementation(Libs.APPCOMPAT)
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)

    implementation(libs.compose.ui)
    implementation(libs.compose.material)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)

    implementation(libs.kotlin.coroutines)
    implementation(Libs.LIFECYCLE_RUNTIME_KTX)
    implementation(Libs.LIFECYCLE_VIEWMODEL_KTX)

    implementation(Libs.FRAGMENT_KTX)

    implementation(Libs.NORDIC_BLE)
    implementation(Libs.NORDIC_BLE_KTX)
    implementation(Libs.NORDIC_BLE_COMMON)
    implementation(Libs.NORDIC_BLE_SCAN)

    implementation(Libs.CICERONE)
}

plugins {
    androidCompose
    id("com.squareup.anvil")
    kotlin("kapt")
}

dependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.markdown)
    implementation(projects.components.core.ui)
    implementation(projects.components.core.navigation)
    implementation(projects.components.core.preference)

    implementation(projects.components.info.api)
    implementation(projects.components.firstpair.api)

    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.synchronization.api)
    implementation(projects.components.bridge.service.api)
    implementation(projects.components.bridge.protobuf)

    // Core deps
    implementation(libs.ktx)
    implementation(libs.annotations)

    implementation(libs.appcompat)
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)

    implementation(libs.compose.ui)
    implementation(libs.compose.material)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)

    implementation(libs.kotlin.coroutines)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.compose)

    implementation(libs.ktx.fragment)

    implementation(libs.ble)
    implementation(libs.ble.common)
    implementation(libs.ble.scan)

    implementation(libs.cicerone)
}

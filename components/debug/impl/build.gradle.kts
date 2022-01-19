plugins {
    androidCompose
    id("com.squareup.anvil")
    kotlin("kapt")
}

dependencies {
    implementation(projects.components.debug.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.ui)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.navigation)

    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.protobuf)
    implementation(projects.components.bridge.service.api)

    implementation(projects.components.bridge.synchronization.api)

    implementation(libs.ble.ktx)

    implementation(libs.annotations)
    implementation(libs.appcompat)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)

    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.compose)
    implementation(libs.ktx.fragment)

    implementation(libs.cicerone)
}

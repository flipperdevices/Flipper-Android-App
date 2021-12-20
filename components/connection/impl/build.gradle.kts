plugins {
    androidCompose
    id("com.squareup.anvil")
    kotlin("kapt")
}

dependencies {
    implementation(projects.components.connection.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.ui)

    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.service.api)
    implementation(Libs.NORDIC_BLE_KTX)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)

    implementation(Libs.LIFECYCLE_RUNTIME_KTX)
    implementation(Libs.LIFECYCLE_VIEWMODEL_KTX)
    implementation(Libs.LIFECYCLE_COMPOSE)
    implementation(Libs.FRAGMENT_KTX)
}

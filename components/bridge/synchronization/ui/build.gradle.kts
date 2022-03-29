plugins {
    androidCompose
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

dependencies {
    implementation(projects.components.bridge.synchronization.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.ui)

    implementation(projects.components.bridge.dao.api)

    implementation(libs.cicerone)
    implementation(libs.appcompat)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.lifecycle.compose)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}

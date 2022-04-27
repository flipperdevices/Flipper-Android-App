plugins {
    androidCompose
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

dependencies {
    implementation(projects.components.updater.api)
    implementation(projects.components.updater.fonts)

    implementation(projects.components.core.di)
    implementation(projects.components.core.ui)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.preference)

    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.service.api)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)

    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.compose)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}

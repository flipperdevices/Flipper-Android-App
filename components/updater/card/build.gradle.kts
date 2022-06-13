plugins {
    androidCompose
    id("com.squareup.anvil")
    kotlin("kapt")
}

dependencies {
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.log)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.di)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.lifecycle)

    implementation(projects.components.updater.api)
    implementation(projects.components.updater.fonts)
    implementation(projects.components.info.shared)

    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.service.api)

    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.compose)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)

    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}

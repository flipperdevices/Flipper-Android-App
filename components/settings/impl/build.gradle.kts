plugins {
    androidCompose
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

dependencies {
    implementation(projects.components.settings.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.navigation)
    implementation(projects.components.core.preference)
    implementation(projects.components.analytics.shake2report.api)
    implementation(projects.components.analytics.metric.api)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.core.ui.fragment)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.navigation)

    implementation(projects.components.debug.api)
    implementation(projects.components.firstpair.api)
    implementation(projects.components.filemanager.api)
    implementation(projects.components.screenstreaming.api)

    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.pbutils)
    implementation(projects.components.bridge.service.api)
    implementation(projects.components.bridge.synchronization.api)
    implementation(libs.ble.common)

    implementation(libs.cicerone)
    implementation(libs.appcompat)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.navigation)

    implementation(libs.lifecycle.compose)
    implementation(libs.lifecycle.viewmodel.ktx)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
    implementation(libs.tangle.viewmodel.compose)
    implementation(libs.tangle.viewmodel.api)
    anvil(libs.tangle.viewmodel.compiler)
}

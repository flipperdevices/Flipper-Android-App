plugins {
    id("flipper.android-compose")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.debug.stresstest"

dependencies {
    implementation(projects.components.debug.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.core.ui.ktx)

    implementation(libs.appcompat)

    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.pbutils)
    implementation(projects.components.bridge.service.api)
    implementation(libs.ble.common)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)

    implementation(libs.kotlin.immutable.collections)

    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.compose)

    implementation(libs.bundles.decompose)
}

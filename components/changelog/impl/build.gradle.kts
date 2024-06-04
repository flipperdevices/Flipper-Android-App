plugins {
    id("flipper.android-compose")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.changelog.impl"

dependencies {
    implementation(projects.components.changelog.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.ui.decompose)

    implementation(projects.components.updater.api)

    implementation(libs.bundles.decompose)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
}
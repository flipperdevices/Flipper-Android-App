plugins {
    id("flipper.android-compose")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.changelog.impl"

dependencies {
    implementation(projects.components.changelog.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.scrollbar)
    implementation(projects.components.core.markdown)
    implementation(projects.components.core.preference)

    implementation(projects.components.updater.api)
    implementation(projects.components.info.shared)
    implementation(projects.components.keyscreen.shared)
    implementation(projects.components.rootscreen.api)

    implementation(libs.bundles.decompose)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
}

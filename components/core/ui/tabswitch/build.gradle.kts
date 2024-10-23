plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.tabswitch"

commonDependencies {
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.ktx)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
}

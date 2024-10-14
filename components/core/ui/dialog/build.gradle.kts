plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}
val namespace = "com.flipperdevices.core.ui.dialog"
android.namespace = namespace

commonDependencies {
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.theme)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
}

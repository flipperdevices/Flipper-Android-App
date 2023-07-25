plugins {
    id("flipper.android-compose")
}

android.namespace = "com.flipperdevices.faphub.errors"

dependencies {
    implementation(projects.components.core.ui.theme)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
}

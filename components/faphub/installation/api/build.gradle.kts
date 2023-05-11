plugins {
    id("flipper.android-compose")
}

android.namespace = "com.flipperdevices.faphub.installation.api"

dependencies {
    implementation(projects.components.faphub.dao.api)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
}

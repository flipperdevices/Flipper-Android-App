plugins {
    id("flipper.android-compose")
}

android.namespace = "com.flipperdevices.core.ui.errors"

dependencies {
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.theme)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.paging)

    implementation(libs.ktor.client)
}

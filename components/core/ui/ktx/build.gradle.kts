plugins {
    id("flipper.android-compose")
}

android.namespace = "com.flipperdevices.core.ui.ktx"

dependencies {
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.theme)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.placeholder)
    implementation(libs.coil.compose)
    implementation(libs.coil.svg)
    implementation(libs.compose.systemuicontroller)
    implementation(libs.image.lottie)
}

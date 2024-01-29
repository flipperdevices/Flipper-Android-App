plugins {
    id("flipper.android-compose")
}

android.namespace = "com.flipperdevices.nfceditor.api"

dependencies {
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.keyparser.api)
    implementation(projects.components.core.ui.decompose)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.decompose)
}

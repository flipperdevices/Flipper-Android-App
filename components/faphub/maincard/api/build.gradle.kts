plugins {
    id("flipper.android-compose")
}

android.namespace = "com.flipperdevices.faphub.maincard.api"

dependencies {

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.decompose)
}

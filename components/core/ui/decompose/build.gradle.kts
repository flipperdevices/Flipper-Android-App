plugins {
    id("flipper.android-compose")
}

android.namespace = "com.flipperdevices.ui.decompose"

dependencies {
    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
    implementation(libs.lifecycle.viewmodel.ktx)
}

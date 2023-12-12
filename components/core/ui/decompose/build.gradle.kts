plugins {
    id("flipper.android-compose")
}

android.namespace = "com.flipperdevices.ui.decompose"

dependencies {
    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
    implementation(libs.compose.activity)

    implementation(libs.lifecycle.compose)

    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)

    implementation(libs.bundles.decompose)
}

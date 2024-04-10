plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.faphub.main.api"

dependencies {
    implementation(projects.components.core.ui.decompose)

    implementation(projects.components.deeplink.api)

    implementation(libs.compose.ui)
    implementation(libs.decompose)
}

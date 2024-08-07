plugins {
    id("flipper.android-compose")
}

android.namespace = "com.flipperdevices.remotecontrols.categories.api"

dependencies {
    implementation(projects.components.core.ui.decompose)

    implementation(libs.compose.ui)
    implementation(libs.decompose)
}

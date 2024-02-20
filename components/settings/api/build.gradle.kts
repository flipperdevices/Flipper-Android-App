plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.settings.api"

dependencies {
    implementation(projects.components.core.ui.decompose)
    implementation(libs.decompose)
}

plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.debug.api"

dependencies {
    implementation(projects.components.core.ui.decompose)
    implementation(libs.decompose)
}

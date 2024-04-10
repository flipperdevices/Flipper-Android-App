plugins {
    id("flipper.android-lib")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.faphub.screenshotspreview.api"

dependencies {
    implementation(projects.components.core.ui.decompose)

    implementation(libs.decompose)
}

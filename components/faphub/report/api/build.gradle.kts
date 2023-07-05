plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.faphub.report.api"

dependencies {
    implementation(projects.components.core.ui.navigation)
}

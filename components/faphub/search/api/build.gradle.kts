plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.faphub.search.api"

dependencies {
    implementation(projects.components.faphub.screenshotspreview.api)

    implementation(projects.components.core.ui.decompose)

    implementation(libs.decompose)
}

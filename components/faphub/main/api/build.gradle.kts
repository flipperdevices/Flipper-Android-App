plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.faphub.main.api"

dependencies {
    implementation(projects.components.core.ui.navigation)
}

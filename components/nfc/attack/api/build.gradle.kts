plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.nfc.attack.api"

dependencies {
    implementation(projects.components.core.ui.navigation)
    implementation(libs.kotlin.coroutines)
}

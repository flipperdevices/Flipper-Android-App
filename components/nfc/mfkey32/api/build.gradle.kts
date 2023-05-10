plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.nfc.mfkey32.api"

dependencies {
    implementation(projects.components.core.ui.navigation)

    implementation(projects.components.bridge.api)

    implementation(libs.kotlin.coroutines)
}

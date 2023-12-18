plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.nfc.attack.api"

dependencies {
    implementation(projects.components.core.ui.decompose)

    implementation(libs.decompose)

    implementation(libs.kotlin.coroutines)
}

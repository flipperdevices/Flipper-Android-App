plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.nfc.mfkey32.api"

commonDependencies {
    implementation(projects.components.core.ui.decompose)

    implementation(libs.kotlin.coroutines)

    implementation(libs.decompose)
}

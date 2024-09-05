plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.nfc.mfkey32.api"

commonDependencies {
    implementation(projects.components.core.ui.decompose)

    implementation(projects.components.bridge.connection.feature.storage.api)

    implementation(libs.kotlin.coroutines)

    implementation(libs.decompose)
}

plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.remotecontrols.core.model"

commonDependencies {
    implementation(libs.kotlin.serialization.json)
}

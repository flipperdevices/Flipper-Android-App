plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.bridge.connection.config.api"

commonDependencies {
    implementation(projects.components.core.preference)

    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.serialization.json)
}

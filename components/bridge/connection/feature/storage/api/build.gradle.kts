plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.bridge.connection.feature.storage.api"

commonDependencies {
    implementation(projects.components.bridge.connection.feature.common.api)
}

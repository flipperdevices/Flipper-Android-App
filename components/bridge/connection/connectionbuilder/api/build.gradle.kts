plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.bridge.connection.connectionbuilder.api"

commonDependencies {
    implementation(projects.components.bridge.connection.transport.common.api)
    implementation(libs.kotlin.coroutines)
}

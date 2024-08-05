plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.bridge.connection.transport.ble.api"

commonDependencies {
    implementation(projects.components.bridge.connection.transport.common.api)
    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.immutable.collections)
}

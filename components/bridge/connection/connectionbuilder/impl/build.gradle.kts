plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil-multiplatform")
}

android.namespace = "com.flipperdevices.bridge.connection.connectionbuilder.impl"

commonDependencies {
    implementation(projects.components.bridge.connection.connectionbuilder.api)

    implementation(projects.components.core.di)

    implementation(projects.components.bridge.connection.transport.common.api)
    implementation(projects.components.bridge.connection.transport.ble.api)

    implementation(libs.kotlin.coroutines)
}

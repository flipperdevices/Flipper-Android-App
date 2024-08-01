plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil-multiplatform")
}

android.namespace = "com.flipperdevices.bridge.connection.transportconfigbuilder.impl"

commonDependencies {
    implementation(projects.components.bridge.connection.transportconfigbuilder.api)

    implementation(projects.components.core.di)

    implementation(projects.components.bridge.connection.transport.common.api)
    implementation(projects.components.bridge.connection.transport.ble.api)
    implementation(projects.components.bridge.connection.config.api)

    implementation(libs.kotlin.immutable.collections)
}

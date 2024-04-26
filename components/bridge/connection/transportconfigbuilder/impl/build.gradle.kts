plugins {
    id("flipper.android-lib")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.bridge.connection.transportconfigbuilder.impl"

dependencies {
    implementation(projects.components.bridge.connection.transportconfigbuilder.api)

    implementation(projects.components.core.di)

    implementation(projects.components.bridge.connection.transport.common.api)
    implementation(projects.components.bridge.connection.transport.ble.api)
    implementation(projects.components.bridge.connection.config.api)
}

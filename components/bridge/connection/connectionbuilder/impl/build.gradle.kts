plugins {
    id("flipper.android-lib")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.bridge.connection.connectionbuilder.impl"

dependencies {
    implementation(projects.components.bridge.connection.connectionbuilder.api)

    implementation(projects.components.core.di)

    implementation(projects.components.bridge.connection.common.api)
    implementation(projects.components.bridge.connection.ble.api)

    implementation(libs.kotlin.coroutines)
}

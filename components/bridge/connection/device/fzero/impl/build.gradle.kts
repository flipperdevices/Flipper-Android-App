plugins {
    id("flipper.android-lib")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.bridge.connection.device.fzero.impl"

dependencies {
    implementation(projects.components.bridge.connection.device.fzero.api)

    implementation(projects.components.core.di)

    implementation(projects.components.bridge.connection.device.common.api)
    implementation(projects.components.bridge.connection.feature.common.api)
}

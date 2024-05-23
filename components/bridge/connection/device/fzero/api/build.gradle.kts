plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.bridge.connection.device.fzero.api"

dependencies {
    implementation(projects.components.bridge.connection.device.common.api)
    implementation(projects.components.bridge.connection.feature.common.api)
    implementation(projects.components.bridge.connection.transport.common.api)

    implementation(libs.kotlin.coroutines)
}

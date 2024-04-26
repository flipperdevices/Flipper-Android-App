plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.bridge.connection.connectionbuilder.api"

dependencies {
    implementation(projects.components.bridge.connection.transport.common.api)
    implementation(libs.kotlin.coroutines)
}

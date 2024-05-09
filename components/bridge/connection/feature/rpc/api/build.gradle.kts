plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.bridge.connection.feature.rpc.api"

dependencies {
    implementation(projects.components.bridge.connection.feature.common.api)
}

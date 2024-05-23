plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.bridge.connection.feature.seriallagsdetector.api"

dependencies {
    implementation(projects.components.bridge.connection.feature.common.api)

    implementation(projects.components.bridge.connection.feature.rpc.model)
    implementation(projects.components.bridge.connection.feature.restartrpc.api)

    implementation(libs.kotlin.coroutines)
}

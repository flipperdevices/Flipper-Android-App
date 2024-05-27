plugins {
    id("flipper.android-lib")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.bridge.connection.feature.restartrpc.impl"

dependencies {
    implementation(projects.components.bridge.connection.feature.restartrpc.api)

    implementation(projects.components.bridge.connection.feature.common.api)
    implementation(projects.components.bridge.connection.transport.common.api)

    implementation(projects.components.core.di)
    implementation(libs.kotlin.coroutines)
}

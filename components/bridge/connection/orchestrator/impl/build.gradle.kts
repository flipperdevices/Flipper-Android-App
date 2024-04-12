plugins {
    id("flipper.android-lib")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.bridge.connection.orchestrator.impl"

dependencies {
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.log)
    implementation(projects.components.core.di)

    implementation(projects.components.bridge.connection.orchestrator.api)
    implementation(projects.components.bridge.connection.ble.api)
    implementation(projects.components.bridge.connection.common.api)
    implementation(projects.components.bridge.connection.connectionbuilder.api)

    implementation(libs.kotlin.coroutines)
}

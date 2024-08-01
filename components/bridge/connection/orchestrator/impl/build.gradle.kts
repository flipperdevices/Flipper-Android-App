plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil-multiplatform")
}

android.namespace = "com.flipperdevices.bridge.connection.orchestrator.impl"

commonDependencies {
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.log)
    implementation(projects.components.core.di)

    implementation(projects.components.bridge.connection.orchestrator.api)
    implementation(projects.components.bridge.connection.transport.ble.api)
    implementation(projects.components.bridge.connection.transport.common.api)
    implementation(projects.components.bridge.connection.connectionbuilder.api)
    implementation(projects.components.bridge.connection.config.api)
    implementation(projects.components.bridge.connection.transportconfigbuilder.api)

    implementation(libs.kotlin.coroutines)
}

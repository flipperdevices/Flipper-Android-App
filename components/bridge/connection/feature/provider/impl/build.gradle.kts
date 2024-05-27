plugins {
    id("flipper.android-lib")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.bridge.connection.feature.provider.impl"

dependencies {
    implementation(projects.components.bridge.connection.feature.provider.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.ktx)

    implementation(projects.components.bridge.connection.config.api)
    implementation(projects.components.bridge.connection.device.common.api)
    implementation(projects.components.bridge.connection.device.fzero.api)
    implementation(projects.components.bridge.connection.feature.common.api)
    implementation(projects.components.bridge.connection.feature.rpc.api)
    implementation(projects.components.bridge.connection.orchestrator.api)
    implementation(projects.components.bridge.connection.transport.common.api)

    implementation(libs.kotlin.immutable.collections)
    implementation(libs.kotlin.coroutines)
}

plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil-multiplatform")
}

android.namespace = "com.flipperdevices.bridge.connection.device.fzero.impl"

commonDependencies {
    implementation(projects.components.bridge.connection.device.fzero.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.buildKonfig)

    implementation(projects.components.bridge.connection.device.common.api)
    implementation(projects.components.bridge.connection.feature.common.api)
    implementation(projects.components.bridge.connection.transport.common.api)

    implementation(projects.components.bridge.connection.feature.rpc.api)
    implementation(projects.components.bridge.connection.feature.restartrpc.api)
    implementation(projects.components.bridge.connection.feature.lagsdetector.api)
    implementation(projects.components.bridge.connection.feature.serialspeed.api)
    implementation(projects.components.bridge.connection.feature.protocolversion.api)
    implementation(projects.components.bridge.connection.feature.rpcinfo.api)
    implementation(projects.components.bridge.connection.feature.storage.api)
    implementation(projects.components.bridge.connection.feature.storageinfo.api)
    implementation(projects.components.bridge.connection.feature.getinfo.api)

    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.immutable.collections)
}

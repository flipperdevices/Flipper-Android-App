plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil-multiplatform")
}

android.namespace = "com.flipperdevices.bridge.connection.feature.storage.impl"

commonDependencies {
    implementation(projects.components.bridge.connection.feature.storage.api)

    implementation(projects.components.core.di)

    implementation(projects.components.bridge.connection.transport.common.api)
    implementation(projects.components.bridge.connection.feature.common.api)
    implementation(projects.components.bridge.connection.feature.rpc.api)
    implementation(projects.components.bridge.connection.pbutils)

    implementation(libs.kotlin.coroutines)
}

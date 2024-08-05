plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil-multiplatform")
}

android.namespace = "com.flipperdevices.bridge.connection.feature.restartrpc.impl"

commonDependencies {
    implementation(projects.components.bridge.connection.feature.restartrpc.api)

    implementation(projects.components.bridge.connection.feature.common.api)
    implementation(projects.components.bridge.connection.transport.common.api)

    implementation(projects.components.core.di)
    implementation(libs.kotlin.coroutines)
}

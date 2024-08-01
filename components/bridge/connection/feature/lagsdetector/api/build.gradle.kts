plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.bridge.connection.feature.seriallagsdetector.api"

commonDependencies {
    implementation(projects.components.bridge.connection.feature.common.api)

    implementation(projects.components.bridge.connection.feature.rpc.model)
    implementation(projects.components.bridge.connection.feature.restartrpc.api)

    implementation(libs.kotlin.coroutines)
}

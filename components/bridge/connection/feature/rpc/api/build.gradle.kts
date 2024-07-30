plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.bridge.connection.feature.rpc.api"

commonDependencies {
    api(projects.components.bridge.connection.feature.rpc.model)

    implementation(projects.components.core.ktx)

    implementation(projects.components.bridge.connection.feature.common.api)
    implementation(projects.components.bridge.connection.transport.common.api)
    implementation(projects.components.bridge.connection.feature.lagsdetector.api)

    implementation(projects.components.bridge.connection.pbutils)

    implementation(libs.kotlin.coroutines)
}

plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.bridge.connection.feature.rpc.api"

dependencies {
    api(projects.components.bridge.connection.feature.rpc.model)

    implementation(projects.components.core.ktx)

    implementation(projects.components.bridge.connection.feature.common.api)
    implementation(projects.components.bridge.connection.feature.lagsdetector.api)
    implementation(projects.components.bridge.connection.transport.common.api)

    implementation(projects.components.bridge.pbutils)

    implementation(libs.kotlin.coroutines)
}

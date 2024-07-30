plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.bridge.connection.feature.protocolversion.api"

dependencies {
    implementation(projects.components.core.data)
    implementation(projects.components.core.ktx)

    implementation(projects.components.bridge.connection.feature.common.api)
    implementation(projects.components.bridge.connection.transport.common.api)

    implementation(libs.kotlin.immutable.collections)
    implementation(libs.kotlin.coroutines)
}

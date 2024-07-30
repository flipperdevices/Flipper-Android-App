plugins {
    id("flipper.android-lib")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.bridge.connection.feature.protocolversion.impl"

dependencies {
    implementation(projects.components.bridge.connection.feature.protocolversion.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.data)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.log)
    implementation(projects.components.core.preference)

    implementation(projects.components.bridge.connection.feature.common.api)
    implementation(projects.components.bridge.connection.transport.common.api)

    implementation(libs.kotlin.immutable.collections)
    implementation(libs.kotlin.coroutines)
}

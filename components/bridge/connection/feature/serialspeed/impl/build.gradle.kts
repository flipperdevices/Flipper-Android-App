plugins {
    id("flipper.android-lib")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.bridge.connection.feature.serialspeed.impl"

dependencies {
    implementation(projects.components.bridge.connection.feature.serialspeed.api)

    implementation(projects.components.core.di)

    implementation(projects.components.bridge.connection.feature.common.api)
    implementation(projects.components.bridge.connection.transport.common.api)

    implementation(libs.kotlin.coroutines)
}

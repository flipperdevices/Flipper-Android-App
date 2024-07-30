plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.bridge.connection.feature.common.api"

dependencies {
    implementation(projects.components.core.di)

    implementation(projects.components.bridge.connection.transport.common.api)

    implementation(libs.kotlin.coroutines)
    implementation(libs.dagger)
}

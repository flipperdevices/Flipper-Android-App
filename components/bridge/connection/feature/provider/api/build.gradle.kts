plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.bridge.connection.feature.provider.api"

dependencies {
    implementation(projects.components.bridge.connection.feature.common.api)

    implementation(libs.kotlin.coroutines)
}

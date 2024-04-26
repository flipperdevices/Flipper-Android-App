plugins {
    id("flipper.android-lib")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.bridge.connection.config.api"

dependencies {
    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.serialization.json)
}

plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.bridge.connection.config.api"

dependencies {
    implementation(libs.kotlin.coroutines)
}

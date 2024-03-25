plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.bridge.connection.common.api"

dependencies {
    implementation(libs.kotlin.coroutines)
}

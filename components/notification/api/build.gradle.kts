plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.notification.api"

dependencies {
    implementation(libs.kotlin.coroutines)
}
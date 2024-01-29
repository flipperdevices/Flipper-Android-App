plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.selfupdater.api"

dependencies {
    implementation(libs.kotlin.coroutines)
}

plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.faphub.installation.queue.api"

dependencies {
    implementation(libs.kotlin.coroutines)
}

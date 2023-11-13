plugins {
    id("flipper.android-compose")
}

android.namespace = "com.flipperdevices.notification.api"

dependencies {
    implementation(libs.kotlin.coroutines)

    // Compose
    implementation(libs.compose.ui)
}

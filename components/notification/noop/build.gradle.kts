plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.notification.noop"

dependencies {
    implementation(projects.components.notification.api)
}
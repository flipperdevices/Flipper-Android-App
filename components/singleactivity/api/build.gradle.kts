plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.singleactivity.api"

dependencies {
    implementation(projects.components.deeplink.api)
}

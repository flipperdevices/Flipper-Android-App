plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.wearable.sync.handheld.api"

dependencies {
    implementation(projects.components.bridge.dao.api)
}

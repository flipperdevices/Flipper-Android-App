plugins {
    id("flipper.android-lib")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.wearable.sync.handheld.noop"

dependencies {
    implementation(projects.components.wearable.sync.handheld.api)

    implementation(projects.components.core.di)
}

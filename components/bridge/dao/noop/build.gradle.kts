plugins {
    id("flipper.android-lib")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.bridge.dao.noop"

dependencies {
    implementation(projects.components.bridge.dao.api)

    implementation(projects.components.core.di)
}

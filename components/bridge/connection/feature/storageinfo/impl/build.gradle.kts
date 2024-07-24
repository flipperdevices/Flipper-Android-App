plugins {
    id("flipper.android-lib")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.bridge.conenction.feature.storageinfo.impl"

dependencies {
    implementation(projects.components.bridge.connection.feature.storageinfo.api)

    implementation(projects.components.core.di)
}
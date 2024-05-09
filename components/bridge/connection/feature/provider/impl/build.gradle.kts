plugins {
    id("flipper.android-lib")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.bridge.connection.feature.provider.impl"

dependencies {
    implementation(projects.components.bridge.connection.feature.provider.api)

    implementation(projects.components.core.di)
}

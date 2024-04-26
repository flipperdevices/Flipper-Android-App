plugins {
    id("flipper.android-lib")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.bridge.connection.config.impl"

dependencies {
    implementation(projects.components.bridge.connection.config.api)

    implementation(projects.components.core.log)
    implementation(projects.components.core.di)
    implementation(projects.components.core.preference)

    implementation(libs.kotlin.coroutines)
}

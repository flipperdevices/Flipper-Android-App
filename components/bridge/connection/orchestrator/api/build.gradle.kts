plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.bridge.connection.orchestrator.api"

dependencies {
    implementation(projects.components.bridge.connection.common.api)
}

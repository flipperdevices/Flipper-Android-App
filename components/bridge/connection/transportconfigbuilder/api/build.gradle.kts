plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.bridge.connection.transportconfigbuilder.api"

commonDependencies {
    implementation(projects.components.bridge.connection.transport.common.api)
    implementation(projects.components.bridge.connection.config.api)
}

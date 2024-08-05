plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.bridge.connection.orchestrator.api"

commonDependencies {
    implementation(projects.components.bridge.connection.transport.common.api)

    implementation(projects.components.bridge.connection.config.api)

    implementation(libs.kotlin.coroutines)
}

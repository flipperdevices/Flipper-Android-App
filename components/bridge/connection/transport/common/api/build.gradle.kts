plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.bridge.connection.transport.common.api"

commonDependencies {
    implementation(projects.components.bridge.connection.feature.actionnotifier.api)

    implementation(libs.kotlin.coroutines)
}

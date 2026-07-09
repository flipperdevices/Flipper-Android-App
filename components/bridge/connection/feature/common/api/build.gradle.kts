plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("dev.zacsweers.metro")
}

android.namespace = "com.flipperdevices.bridge.connection.feature.common.api"

commonDependencies {
    implementation(projects.components.core.di)

    implementation(projects.components.bridge.connection.transport.common.api)

    implementation(libs.kotlin.coroutines)
}

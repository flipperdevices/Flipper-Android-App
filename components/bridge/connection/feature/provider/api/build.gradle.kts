plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.bridge.connection.feature.provider.api"

commonDependencies {
    implementation(projects.components.bridge.connection.feature.common.api)

    implementation(libs.kotlin.coroutines)
}

plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.bridge.connection.feature.storage.api"

commonDependencies {
    implementation(projects.components.core.progress)

    implementation(projects.components.bridge.connection.feature.common.api)

    implementation(libs.kotlin.coroutines)
    implementation(libs.okio)
}

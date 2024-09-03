plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.bridge.synchronization.api"

commonDependencies {
    implementation(projects.components.bridge.dao.api)

    implementation(libs.compose.ui)
    implementation(libs.decompose)
}

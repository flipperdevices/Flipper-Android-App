plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil-multiplatform")
}

android.namespace = "com.flipperdevices.bridge.synchronization.stub"

commonDependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.synchronization.api)

    implementation(libs.lifecycle.compose)
    implementation(libs.decompose)
}

plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.bridge.connection.feature.rpc.model"

commonDependencies {
    implementation(projects.components.core.ktx)
    implementation(projects.components.bridge.connection.pbutils)
}

plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.bridge.connection.feature.rpcstats.api"

commonDependencies {
    implementation(projects.components.bridge.connection.feature.common.api)
}

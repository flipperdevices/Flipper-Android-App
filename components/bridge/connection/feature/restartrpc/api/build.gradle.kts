plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}


commonDependencies {
    implementation(projects.components.bridge.connection.feature.common.api)
    implementation(projects.components.bridge.connection.transport.common.api)
}

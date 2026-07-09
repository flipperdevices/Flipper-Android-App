plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}


commonDependencies {
    implementation(projects.components.bridge.connection.feature.common.api)
}

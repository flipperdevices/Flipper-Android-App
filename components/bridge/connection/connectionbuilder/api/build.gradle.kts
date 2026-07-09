plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}


commonDependencies {
    implementation(projects.components.bridge.connection.transport.common.api)
    implementation(libs.kotlin.coroutines)
}

plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

commonDependencies {
    implementation(projects.components.bridge.connection.orchestrator.api)
    implementation(projects.components.bridge.connection.config.api)

    implementation(libs.kotlin.coroutines)
}

plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
}


commonDependencies {
    implementation(projects.components.bridge.connection.feature.restartrpc.api)

    implementation(projects.components.bridge.connection.feature.common.api)
    implementation(projects.components.bridge.connection.transport.common.api)

    implementation(projects.components.core.di)
    implementation(libs.kotlin.coroutines)
}

plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("dev.zacsweers.metro")
}

commonDependencies {
    implementation(projects.components.core.di)

    implementation(projects.components.bridge.connection.transport.common.api)

    implementation(libs.kotlin.coroutines)
}

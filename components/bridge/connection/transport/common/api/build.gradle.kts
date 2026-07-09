plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}


commonDependencies {
    implementation(projects.components.bridge.connection.feature.actionnotifier.api)

    implementation(libs.kotlin.coroutines)
}

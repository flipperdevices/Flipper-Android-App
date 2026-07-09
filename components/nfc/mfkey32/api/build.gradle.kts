plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

androidDependencies {
    implementation(projects.components.core.ui.decompose)
    implementation(libs.bundles.decompose)

    implementation(projects.components.bridge.connection.feature.storage.api)

    implementation(libs.kotlin.coroutines)
}

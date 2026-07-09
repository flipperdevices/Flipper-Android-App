plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

commonDependencies {
    implementation(libs.metro.utils.annotations)
    implementation(projects.components.bridge.connection.feature.storage.api)

    implementation(projects.components.core.ui.decompose)

    implementation(libs.decompose)

    implementation(libs.okio)

    implementation(libs.kotlin.coroutines)
}

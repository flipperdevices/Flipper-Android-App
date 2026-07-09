plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

commonDependencies {
    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.deeplink.api)

    implementation(projects.components.bridge.connection.feature.storage.api)

    implementation(libs.decompose)

    implementation(libs.okio)
}

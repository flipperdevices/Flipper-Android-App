plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}

androidDependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.connection.feature.emulate.api)

    // Compose
    implementation(libs.decompose)
}

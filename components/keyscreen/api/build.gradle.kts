plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
}

androidDependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.keyparser.api)

    implementation(projects.components.bridge.connection.feature.emulate.api)

    // Compose
    implementation(libs.decompose)

    implementation(projects.components.keyemulate.api)
}

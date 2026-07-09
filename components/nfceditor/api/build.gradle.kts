plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}

androidDependencies {
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.keyparser.api)
    implementation(projects.components.core.ui.decompose)

    // Compose
    implementation(libs.decompose)
}

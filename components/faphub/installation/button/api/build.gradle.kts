plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}

androidDependencies {
    implementation(projects.components.faphub.dao.api)

    implementation(projects.components.core.data)

    // Compose
    implementation(libs.decompose)
}

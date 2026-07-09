plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
}

commonDependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.synchronization.api)

    implementation(libs.lifecycle.compose)
    implementation(libs.decompose)
}

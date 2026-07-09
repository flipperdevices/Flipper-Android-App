plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
}

androidDependencies {
    implementation(projects.components.screenstreaming.api)
    implementation(projects.components.core.ui.decompose)

    implementation(libs.bundles.decompose)

    implementation(projects.components.core.di)
}

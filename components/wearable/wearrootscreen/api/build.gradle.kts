plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
    id("kotlinx-serialization")
}

androidDependencies {
    implementation(libs.metro.utils.annotations)
    implementation(projects.components.core.ui.decompose)

    implementation(projects.components.bridge.dao.api)

    implementation(libs.bundles.decompose)
}

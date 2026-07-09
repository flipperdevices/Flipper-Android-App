plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}


commonDependencies {
    implementation(projects.components.bridge.dao.api)

    implementation(libs.compose.ui)
    implementation(libs.decompose)
}

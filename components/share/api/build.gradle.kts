plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}


androidDependencies {
    implementation(projects.components.deeplink.api)
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.core.ui.decompose)

    implementation(libs.decompose)
}

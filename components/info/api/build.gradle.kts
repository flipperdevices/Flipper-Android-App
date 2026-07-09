plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

androidDependencies {
    implementation(projects.components.deeplink.api)

    implementation(projects.components.core.ui.decompose)

    implementation(projects.components.deeplink.api)

    implementation(libs.decompose)
}

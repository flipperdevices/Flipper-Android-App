plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}


androidDependencies {
    implementation(projects.components.core.ui.decompose)

    // Compose
    implementation(libs.decompose)

    implementation(projects.components.wearable.wearrootscreen.api)
}

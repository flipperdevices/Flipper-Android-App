plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}


androidDependencies {
    implementation(projects.components.core.ui.decompose)

    implementation(libs.decompose)
}

plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}


androidDependencies {
    implementation(projects.components.core.ui.decompose)
    implementation(libs.decompose)
}

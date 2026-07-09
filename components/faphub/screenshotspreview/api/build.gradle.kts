plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("kotlinx-serialization")
}


androidDependencies {
    implementation(projects.components.core.ui.decompose)
    implementation(libs.kotlin.immutable.collections)
    implementation(libs.decompose)
}

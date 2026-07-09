plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

commonDependencies {
    implementation(projects.components.core.ui.decompose)

    implementation(libs.kotlin.coroutines)

    implementation(libs.decompose)
}

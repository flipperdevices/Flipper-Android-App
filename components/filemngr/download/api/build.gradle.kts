plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

commonDependencies {
    implementation(projects.components.core.ui.decompose)

    implementation(libs.decompose)

    implementation(libs.okio)
    implementation(libs.kotlin.coroutines)
}

plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}


androidDependencies {
    implementation(projects.components.core.data)

    implementation(projects.components.faphub.dao.api)
    implementation(projects.components.faphub.installation.manifest.api)

    implementation(libs.kotlin.coroutines)
    implementation(libs.annotations)

    implementation(libs.compose.ui)
}

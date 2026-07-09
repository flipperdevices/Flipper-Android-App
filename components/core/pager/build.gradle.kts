plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

androidDependencies {
    implementation(projects.components.core.log)

    implementation(libs.compose.paging)
}

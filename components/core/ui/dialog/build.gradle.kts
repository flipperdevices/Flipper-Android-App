plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}

commonDependencies {
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.theme)
}

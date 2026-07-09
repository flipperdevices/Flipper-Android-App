plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}

commonDependencies {
    implementation(projects.components.core.ui.theme)

    implementation(projects.components.core.preference)
}

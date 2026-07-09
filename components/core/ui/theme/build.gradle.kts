plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
}

commonDependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.preference)
}

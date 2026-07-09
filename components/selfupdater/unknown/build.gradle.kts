plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
}


androidDependencies {
    implementation(projects.components.selfupdater.api)

    implementation(projects.components.core.di)
}

plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
}

androidDependencies {
    implementation(projects.components.wearable.sync.handheld.api)

    implementation(projects.components.core.di)
}

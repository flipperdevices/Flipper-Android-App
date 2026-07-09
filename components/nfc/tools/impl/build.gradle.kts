plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
}

androidDependencies {
    implementation(projects.components.nfc.tools.api)
    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
}

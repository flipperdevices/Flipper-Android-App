plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}


androidDependencies {
    implementation(projects.components.bridge.dao.api)
}

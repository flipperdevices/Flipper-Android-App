plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

commonDependencies {
    implementation(projects.components.core.ktx)
    implementation(projects.components.bridge.connection.pbutils)
}

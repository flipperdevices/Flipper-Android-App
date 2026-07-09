plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    alias(libs.plugins.buildkonfig)
}

commonDependencies {
    implementation(projects.components.core.buildKonfig)
}

androidDependencies {
    implementation(libs.timber)
}

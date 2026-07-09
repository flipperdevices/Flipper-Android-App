plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

androidDependencies {
    implementation(projects.components.core.buildKonfig)
    implementation(libs.kotlin.coroutines)
}

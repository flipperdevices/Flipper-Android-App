plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

androidDependencies {
    implementation(projects.components.core.data)

    implementation(libs.kotlin.coroutines)
}

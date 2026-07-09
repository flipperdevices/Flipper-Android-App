plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
}

commonDependencies {
    implementation(projects.components.core.log)
    implementation(projects.components.core.di)

    implementation(libs.okio)
    implementation(libs.kotlin.coroutines)
}

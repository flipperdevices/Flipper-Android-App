plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

commonDependencies {
    implementation(libs.kotlin.coroutines)
}

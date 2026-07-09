plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}


androidDependencies {
    implementation(libs.kotlin.coroutines)
    implementation(libs.decompose)

    // Compose
}

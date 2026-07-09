plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("kotlinx-serialization")
}

commonDependencies {
    implementation(libs.kotlin.serialization.json)
}

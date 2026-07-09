plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("kotlinx-serialization")
}

commonDependencies {
    implementation(projects.components.bridge.dao.api)

    implementation(libs.kotlin.serialization.json)

    implementation(libs.okio)
}

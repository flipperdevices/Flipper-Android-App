plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")

    id("kotlinx-serialization")
}


androidDependencies {
    implementation(projects.components.core.data)
    implementation(projects.components.core.progress)
    implementation(projects.components.core.preference)

    implementation(libs.kotlin.serialization.json)
    implementation(libs.kotlin.immutable.collections)

    implementation(projects.components.faphub.target.api)

    implementation(libs.annotations)
    implementation(libs.compose.ui)
}

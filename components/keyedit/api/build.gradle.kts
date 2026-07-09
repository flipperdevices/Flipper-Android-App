plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")

    id("kotlinx-serialization")
}


androidDependencies {
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.core.ktx)

    implementation(libs.kotlin.serialization.json)
    implementation(libs.decompose)
}

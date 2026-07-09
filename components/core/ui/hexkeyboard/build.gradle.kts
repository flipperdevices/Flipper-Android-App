plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}


commonDependencies {
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.data)

    implementation(libs.kotlin.immutable.collections)

    // Compose
    implementation(libs.compose.constraint)
    implementation(libs.compose.material.icons.core)
}

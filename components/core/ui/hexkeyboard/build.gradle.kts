plugins {
    id("flipper.android-compose")
}

dependencies {
    implementation(projects.components.core.ui.ktx)

    implementation(libs.kotlin.immutable.collections)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.constraint)
}

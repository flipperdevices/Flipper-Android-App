plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.core.ui.hexkeyboard"

commonDependencies {
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.data)

    implementation(libs.kotlin.immutable.collections)

    // Compose
    implementation(libs.compose.constraint)
}

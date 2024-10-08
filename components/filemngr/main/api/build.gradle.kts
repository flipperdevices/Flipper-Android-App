plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.filemanager.main.api"

commonDependencies {
    implementation(projects.components.core.ui.decompose)

    implementation(libs.compose.ui)
    implementation(libs.decompose)
}
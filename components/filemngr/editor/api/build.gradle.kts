plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.filemanager.editor.api"

commonDependencies {
    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.bridge.connection.feature.storage.api)

    implementation(libs.compose.ui)
    implementation(libs.decompose)
    implementation(libs.okio)
}

plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.filemanager.rename.api"

commonDependencies {
    implementation(projects.components.bridge.connection.feature.storage.api)

    implementation(projects.components.core.ui.decompose)

    implementation(libs.compose.ui)
    implementation(libs.decompose)

    implementation(libs.okio)
}

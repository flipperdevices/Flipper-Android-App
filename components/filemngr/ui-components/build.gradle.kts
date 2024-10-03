plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.filemanager.ui.components"

androidDependencies {
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)

    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.dialog)
    implementation(projects.components.core.preference)

    implementation(projects.components.bridge.connection.feature.storage.api)
    implementation(projects.components.bridge.dao.api)

    implementation(libs.okio)
    implementation(libs.okio.fake)
    implementation(libs.kotlin.immutable.collections)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
    implementation(libs.compose.tooling)
    implementation(libs.compose.material)
    implementation(libs.compose.material.icons.core)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.placeholder)

    implementation(libs.bundles.decompose)
}

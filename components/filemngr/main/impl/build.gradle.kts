plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil-multiplatform")
    id("kotlinx-serialization")
}
android.namespace = "com.flipperdevices.filemanager.main.impl"

commonDependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.log)

    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.res)

    implementation(projects.components.bridge.connection.feature.storage.api)
    implementation(projects.components.bridge.dao.api)

    implementation(projects.components.filemngr.uiComponents)
    implementation(projects.components.filemngr.main.api)
    implementation(projects.components.filemngr.listing.api)
    implementation(projects.components.filemngr.upload.api)
    implementation(projects.components.filemngr.search.api)
    implementation(projects.components.filemngr.editor.api)
    implementation(projects.components.filemngr.util)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.material.icons.core)
    implementation(libs.compose.material.icons.extended)

    implementation(libs.kotlin.serialization.json)
    implementation(libs.ktor.client)

    implementation(libs.decompose)
    implementation(libs.kotlin.coroutines)
    implementation(libs.essenty.lifecycle)
    implementation(libs.essenty.lifecycle.coroutines)

    implementation(libs.bundles.decompose)
    implementation(libs.okio)
}

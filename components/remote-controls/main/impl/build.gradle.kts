plugins {
    id("flipper.android-compose")
    id("flipper.anvil")
    id("kotlinx-serialization")
}
android.namespace = "com.flipperdevices.remotecontrols.device.select.impl"

dependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.log)

    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.res)

    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.service.api)
    implementation(projects.components.bridge.pbutils)
    implementation(projects.components.bridge.api)
    implementation(projects.components.keyemulate.api)
    implementation(projects.components.infrared.utils)

    implementation(projects.components.remoteControls.apiBackend)
    implementation(projects.components.remoteControls.coreModel)
    implementation(projects.components.remoteControls.coreUi)
    implementation(projects.components.remoteControls.main.api)
    implementation(projects.components.remoteControls.brands.api)
    implementation(projects.components.remoteControls.categories.api)
    implementation(projects.components.remoteControls.grid.main.api)
    implementation(projects.components.remoteControls.setup.api)

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
}

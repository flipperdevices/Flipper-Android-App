plugins {
    id("flipper.android-compose")
    id("flipper.anvil")
    id("kotlinx-serialization")
}
group = "com.flipperdevices.remotecontrols.brands.impl"
android.namespace = "$group"

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
    implementation(projects.components.deeplink.api)
    implementation(projects.components.bridge.service.api)
    implementation(projects.components.bridge.pbutils)
    implementation(projects.components.bridge.api)
    implementation(projects.components.keyemulate.api)
    implementation(projects.components.infrared.editor) // todo

    implementation(projects.components.remoteControls.apiBackend)
    implementation(projects.components.remoteControls.coreModel)
    implementation(projects.components.remoteControls.coreUi)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.material.icons.core)
    implementation(libs.compose.material.icons.extended)

    implementation(libs.kotlin.serialization.json)
    implementation(libs.ktor.client)

    api(libs.decompose)
    implementation(libs.kotlin.coroutines)
    api(libs.essenty.lifecycle)
    implementation(libs.essenty.lifecycle.coroutines)

    implementation(libs.bundles.decompose)
}

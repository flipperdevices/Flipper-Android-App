plugins {
    id("flipper.android-compose")
}

android.namespace = "com.flipperdevices.remotecontrols.core.ui"

dependencies {
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.log)

    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.core.ui.ktx)

    implementation(projects.components.remoteControls.apiBackend)
    implementation(projects.components.remoteControls.coreModel)


    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.material.icons.core)
    implementation(libs.compose.material.icons.extended)

    api(libs.decompose)
    implementation(libs.kotlin.coroutines)
    api(libs.essenty.lifecycle)
    implementation(libs.essenty.lifecycle.coroutines)

    implementation(libs.bundles.decompose)
}
plugins {
    id("flipper.android-compose")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.screenstreaming.impl"

dependencies {
    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.service.api)
    implementation(projects.components.screenstreaming.api)
    implementation(projects.components.bridge.pbutils)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.data)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.share)
    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.dialog)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.core.ui.res)

    implementation(libs.annotations)
    implementation(libs.appcompat)

    implementation(libs.kotlin.immutable.collections)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.constraint)
    implementation(libs.bundles.decompose)

    implementation(libs.kotlin.coroutines)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.compose)
}

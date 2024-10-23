plugins {
    id("flipper.android-compose")
    id("flipper.anvil")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.filemanager.impl"

dependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.storage)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.core.share)

    implementation(projects.components.bridge.service.api)
    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.pbutils)

    implementation(projects.components.filemanager.api)

    implementation(libs.kotlin.serialization.json)

    implementation(projects.components.deeplink.api)

    implementation(projects.components.bottombar.api)

    implementation(libs.annotations)
    implementation(libs.appcompat)

    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.activity)
    implementation(libs.bundles.decompose)

    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.immutable.collections)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
}

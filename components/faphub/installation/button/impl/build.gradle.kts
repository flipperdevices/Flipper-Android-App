plugins {
    id("flipper.android-compose")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.faphub.installation.button.impl"

dependencies {
    implementation(projects.components.faphub.installation.button.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.data)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.dialog)
    implementation(projects.components.core.ui.lifecycle)

    implementation(projects.components.screenstreaming.api)
    implementation(projects.components.rootscreen.api)
    implementation(projects.components.deeplink.api)

    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.service.api)
    implementation(projects.components.bridge.pbutils)

    implementation(projects.components.faphub.installation.stateprovider.api)
    implementation(projects.components.faphub.installation.manifest.api)
    implementation(projects.components.faphub.installation.queue.api)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.decompose)
    implementation(libs.lifecycle.compose)

    implementation(projects.components.faphub.dao.api)

    implementation(libs.kotlin.coroutines)
    implementation(libs.lifecycle.viewmodel.ktx)
}

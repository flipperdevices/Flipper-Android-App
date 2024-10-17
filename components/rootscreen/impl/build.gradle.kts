plugins {
    id("flipper.android-compose")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.rootscreen.impl"

dependencies {
    implementation(projects.components.faphub.screenshotspreview.api)

    implementation(projects.components.rootscreen.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.decompose)

    implementation(projects.components.deeplink.api)

    implementation(projects.components.firstpair.api)
    implementation(projects.components.bottombar.api)
    implementation(projects.components.updater.api)
    implementation(projects.components.screenstreaming.api)
    implementation(projects.components.widget.api)
    implementation(projects.components.share.api)
    implementation(projects.components.keyscreen.api)
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.changelog.api)

    implementation(projects.components.remoteControls.main.api)
    implementation(projects.components.remoteControls.brands.api)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.bundles.decompose)
    implementation(libs.bundles.essenty)
}

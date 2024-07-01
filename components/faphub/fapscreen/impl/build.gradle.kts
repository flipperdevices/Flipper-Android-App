plugins {
    id("flipper.android-compose")
    id("flipper.anvil")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.faphub.fapscreen.impl"

dependencies {
    implementation(projects.components.faphub.screenshotspreview.api)
    implementation(projects.components.faphub.fapscreen.api)
    implementation(projects.components.faphub.errors.api)

    implementation(projects.components.deeplink.api)
    implementation(projects.components.bottombar.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.data)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.markdown)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.dialog)
    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.core.ui.lifecycle)

    implementation(projects.components.analytics.metric.api)

    implementation(projects.components.bridge.dao.api)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.compose)
    implementation(libs.bundles.decompose)

    implementation(projects.components.inappnotification.api)
    implementation(projects.components.rootscreen.api)
    implementation(projects.components.deeplink.api)

    implementation(projects.components.faphub.search.api)
    implementation(projects.components.faphub.dao.api)
    implementation(projects.components.faphub.appcard.composable)
    implementation(projects.components.faphub.installation.stateprovider.api)
    implementation(projects.components.faphub.installation.queue.api)
    implementation(projects.components.faphub.installation.button.api)
    implementation(projects.components.faphub.target.api)
    implementation(projects.components.faphub.report.api)
    implementation(projects.components.faphub.main.api)
    implementation(projects.components.faphub.uninstallbutton.api)

    implementation(libs.kotlin.immutable.collections)
}

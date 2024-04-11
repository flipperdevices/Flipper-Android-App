plugins {
    id("flipper.android-compose")
    id("flipper.anvil")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.faphub.screenshotspreview.impl"

dependencies {
    implementation(projects.components.faphub.screenshotspreview.api)

    implementation(projects.components.core.share)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.core.ui.lifecycle)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.bundles.decompose)

    implementation(libs.zoomable)

    implementation(projects.components.faphub.appcard.composable)

    implementation(libs.kotlin.immutable.collections)
}

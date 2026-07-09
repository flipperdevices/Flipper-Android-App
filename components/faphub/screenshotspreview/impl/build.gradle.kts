plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.faphub.screenshotspreview.impl"

androidDependencies {
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

    implementation(libs.bundles.decompose)

    implementation(libs.zoomable)

    implementation(projects.components.faphub.appcard.composable)

    implementation(libs.kotlin.immutable.collections)
}

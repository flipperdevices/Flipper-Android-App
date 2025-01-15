plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.faphub.appcard.composable"

commonDependencies {
    implementation(projects.components.rootscreen.api)

    implementation(projects.components.faphub.screenshotspreview.api)
    implementation(projects.components.faphub.dao.api)
    implementation(projects.components.faphub.errors.api)

    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.theme)

    implementation(libs.kotlin.immutable.collections)

    implementation(libs.coil.compose)
    implementation(libs.compose.paging)
}

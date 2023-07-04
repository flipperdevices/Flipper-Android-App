plugins {
    id("flipper.android-compose")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.faphub.uninstallbutton.impl"

dependencies {
    implementation(projects.components.faphub.uninstallbutton.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.ui.dialog)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.theme)

    implementation(projects.components.faphub.appcard.composable)
    implementation(projects.components.faphub.dao.api)
    implementation(projects.components.faphub.installation.queue.api)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)

    // ViewModel
    implementation(libs.lifecycle.compose)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.tangle.viewmodel.compose)
    implementation(libs.tangle.viewmodel.api)
    anvil(libs.tangle.viewmodel.compiler)
}

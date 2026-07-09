plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
}


androidDependencies {
    implementation(projects.components.faphub.uninstallbutton.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.ui.dialog)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.theme)

    implementation(projects.components.faphub.appcard.composable)
    implementation(projects.components.faphub.dao.api)
    implementation(projects.components.faphub.installation.queue.api)

    // Compose

    // ViewModel
    implementation(libs.lifecycle.compose)
}

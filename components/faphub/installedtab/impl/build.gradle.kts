plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
}

androidDependencies {
    implementation(projects.components.faphub.installedtab.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.data)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.lifecycle)

    implementation(projects.components.faphub.dao.api)
    implementation(projects.components.faphub.appcard.composable)
    implementation(projects.components.faphub.installation.button.api)
    implementation(projects.components.faphub.installation.manifest.api)
    implementation(projects.components.faphub.installation.queue.api)
    implementation(projects.components.faphub.uninstallbutton.api)
    implementation(projects.components.faphub.installation.stateprovider.api)
    implementation(projects.components.faphub.target.api)
    implementation(projects.components.faphub.errors.api)

    implementation(projects.components.inappnotification.api)

    // Compose
    implementation(libs.compose.paging)
    implementation(libs.decompose)
    implementation(libs.coil.compose)

    implementation(libs.kotlin.immutable.collections)
}

androidHostTestDependencies {
    implementation(libs.kotlin.coroutines.test)
    implementation(libs.junit)
    implementation(libs.mockk)
}

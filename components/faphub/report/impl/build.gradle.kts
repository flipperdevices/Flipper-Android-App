plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
    id("kotlinx-serialization")
}

androidDependencies {
    implementation(projects.components.faphub.report.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.core.markdown)

    implementation(projects.components.faphub.dao.api)
    implementation(projects.components.inappnotification.api)

    // Compose
    implementation(libs.bundles.decompose)

    // ViewModel
    implementation(libs.lifecycle.compose)
}

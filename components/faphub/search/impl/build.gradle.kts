plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
    id("kotlinx-serialization")
}


androidDependencies {
    implementation(projects.components.faphub.search.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.pager)
    implementation(projects.components.core.ui.searchbar)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.lifecycle)

    implementation(projects.components.faphub.dao.api)
    implementation(projects.components.faphub.appcard.composable)
    implementation(projects.components.faphub.fapscreen.api)
    implementation(projects.components.faphub.installation.button.api)
    implementation(projects.components.faphub.target.api)
    implementation(projects.components.faphub.errors.api)

    implementation(projects.components.analytics.metric.api)

    implementation(projects.components.bridge.dao.api)

    // Compose
    implementation(libs.compose.paging)
    implementation(libs.bundles.decompose)
}

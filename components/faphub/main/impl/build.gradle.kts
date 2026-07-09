plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
    id("kotlinx-serialization")
}

androidDependencies {
    implementation(projects.components.faphub.main.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.tabswitch)
    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.core.ui.lifecycle)

    implementation(projects.components.faphub.dao.api)
    implementation(projects.components.faphub.search.api)
    implementation(projects.components.faphub.catalogtab.api)
    implementation(projects.components.faphub.installedtab.api)
    implementation(projects.components.faphub.category.api)
    implementation(projects.components.faphub.fapscreen.api)

    implementation(projects.components.deeplink.api)
    implementation(projects.components.analytics.metric.api)

    // Compose
    implementation(libs.lifecycle.compose)

    implementation(libs.bundles.decompose)
}

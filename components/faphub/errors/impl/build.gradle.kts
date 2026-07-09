plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
}

androidDependencies {
    implementation(projects.components.faphub.errors.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.theme)

    implementation(projects.components.rootscreen.api)
    implementation(projects.components.deeplink.api)

    // Compose
    implementation(libs.compose.paging)

    implementation(libs.ktor.client)
}

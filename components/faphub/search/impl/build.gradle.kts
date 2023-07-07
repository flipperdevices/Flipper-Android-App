plugins {
    id("flipper.android-compose")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.faphub.search.impl"

dependencies {
    implementation(projects.components.faphub.search.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.pager)
    implementation(projects.components.core.ui.searchbar)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.navigation)

    implementation(projects.components.faphub.dao.api)
    implementation(projects.components.faphub.appcard.composable)
    implementation(projects.components.faphub.fapscreen.api)
    implementation(projects.components.faphub.installation.button.api)
    implementation(projects.components.faphub.target.api)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.navigation)
    implementation(libs.compose.paging)

    implementation(libs.tangle.viewmodel.compose)
    implementation(libs.tangle.viewmodel.api)
    anvil(libs.tangle.viewmodel.compiler)
}

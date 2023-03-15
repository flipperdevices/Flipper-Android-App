plugins {
    id("flipper.android-compose")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

dependencies {
    implementation(projects.components.selfupdater.api)

    implementation(projects.components.core.ui.dialog)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.preference)

    implementation(libs.app.update)
    implementation(libs.app.update.ktx)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)

    // ViewModel
    implementation(libs.lifecycle.viewmodel.ktx)

    // Dagger deps
    implementation(projects.components.core.di)
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
    implementation(libs.tangle.viewmodel.compose)
    implementation(libs.tangle.viewmodel.api)
    anvil(libs.tangle.viewmodel.compiler)
}

plugins {
    id("flipper.lint")
    id("flipper.android-compose")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

dependencies {
    implementation(projects.components.wearable.emulate.api)
    implementation(projects.components.core.ui.navigation)

    implementation(projects.components.core.di)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.wear.foundation)
    implementation(libs.compose.wear.material)
    implementation(libs.compose.wear.navigation)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}

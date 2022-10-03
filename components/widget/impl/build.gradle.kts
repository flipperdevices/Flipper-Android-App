plugins {
    id("flipper.lint")
    id("flipper.android-compose")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

dependencies {
    implementation(projects.components.widget.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.navigation)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.ui.fragment)
    implementation(projects.components.core.ui.res)

    implementation(projects.components.deeplink.api)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)

    implementation(libs.cicerone)
    implementation(libs.appcompat)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}

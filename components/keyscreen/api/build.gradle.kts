plugins {
    id("flipper.lint")
    id("androidCompose")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

dependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.bridge.dao.api)

    // Compose
    implementation(libs.compose.ui)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)

    implementation(libs.cicerone)
}

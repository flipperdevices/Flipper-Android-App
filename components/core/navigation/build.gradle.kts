plugins {
    id("flipper.lint")
    id("androidLibrary")
    id("com.squareup.anvil")
    kotlin("kapt")
}

dependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.bottombar.api)

    implementation(libs.appcompat)

    implementation(libs.cicerone)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}

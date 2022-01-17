plugins {
    androidLibrary
    id("com.squareup.anvil")
    kotlin("kapt")
}

dependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.bottombar.api)

    implementation(Libs.APPCOMPAT)

    implementation(libs.cicerone)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}

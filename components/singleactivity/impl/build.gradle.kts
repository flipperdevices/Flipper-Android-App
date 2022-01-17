plugins {
    androidLibrary
    id("com.squareup.anvil")
    kotlin("kapt")
}

dependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.ui)
    implementation(projects.components.core.navigation)

    implementation(projects.components.bottombar.api)

    implementation(projects.components.deeplink.api)

    implementation(projects.components.singleactivity.api)

    implementation(Libs.ANNOTATIONS)
    implementation(Libs.APPCOMPAT)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)

    implementation(libs.cicerone)
}

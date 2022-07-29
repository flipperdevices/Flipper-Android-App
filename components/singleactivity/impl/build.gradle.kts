plugins {
    androidLibrary
    id("com.squareup.anvil")
    kotlin("kapt")
}

dependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.navigation)
    implementation(projects.components.core.ui.res)

    implementation(projects.components.bottombar.api)
    implementation(projects.components.deeplink.api)
    implementation(projects.components.singleactivity.api)
    implementation(projects.components.firstpair.api)
    implementation(projects.components.updater.api)
    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.service.api)

    implementation(libs.annotations)
    implementation(libs.appcompat)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)

    implementation(libs.cicerone)
}

plugins {
    id("flipper.lint")
    id("androidLibrary")
    id("com.squareup.anvil")
    kotlin("kapt")
}

dependencies {
    implementation(projects.components.analytics.shake2report.api)
    implementation(projects.components.bridge.api)
    implementation(projects.components.core.di)

    implementation(libs.cicerone)
    implementation(libs.annotations)
    implementation(libs.appcompat)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}

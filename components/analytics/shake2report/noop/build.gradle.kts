plugins {
    androidLibrary
    id("com.squareup.anvil")
    kotlin("kapt")
}

dependencies {
    implementation(projects.components.analytics.shake2report.api)
    implementation(projects.components.core.di)

    implementation(Libs.ANNOTATIONS)
    implementation(Libs.APPCOMPAT)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}

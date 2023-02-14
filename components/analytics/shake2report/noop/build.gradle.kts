plugins {
    id("flipper.android-lib")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

dependencies {
    implementation(projects.components.analytics.shake2report.api)
    implementation(projects.components.bridge.api)
    implementation(projects.components.core.di)
    implementation(projects.components.info.api)

    implementation(libs.cicerone)
    implementation(libs.annotations)
    implementation(libs.appcompat)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}

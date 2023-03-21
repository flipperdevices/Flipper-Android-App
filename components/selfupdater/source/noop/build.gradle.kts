plugins {
    id("flipper.android-lib")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

dependencies {
    implementation(projects.components.selfupdater.api)

    // Dagger deps
    implementation(projects.components.core.di)
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}

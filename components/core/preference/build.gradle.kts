plugins {
    androidLibrary
    id("com.squareup.anvil")
    kotlin("kapt")
}

dependencies {
    implementation(project(":components:core:di"))

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}

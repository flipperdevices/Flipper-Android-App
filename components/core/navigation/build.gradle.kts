plugins {
    androidLibrary
    id("com.squareup.anvil")
    kotlin("kapt")
}

dependencies {
    implementation(project(":components:core:di"))
    implementation(project(":components:bottombar:api"))

    implementation(Libs.APPCOMPAT)

    implementation(Libs.CICERONE)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}

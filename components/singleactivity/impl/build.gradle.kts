plugins {
    androidLibrary
    id("com.squareup.anvil")
    kotlin("kapt")
}

dependencies {
    implementation(project(":components:core:di"))
    implementation(project(":components:core:ui"))
    implementation(project(":components:core:navigation"))

    implementation(project(":components:bottombar:api"))

    implementation(project(":components:deeplink:api"))

    implementation(project(":components:singleactivity:api"))

    implementation(Libs.ANNOTATIONS)
    implementation(Libs.APPCOMPAT)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)

    implementation(Libs.CICERONE)
}

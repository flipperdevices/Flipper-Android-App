plugins {
    androidLibrary
}

dependencies {
    implementation(project(":components:deeplink:api"))

    implementation(Libs.CICERONE)
}

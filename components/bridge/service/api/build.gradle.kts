plugins {
    androidLibrary
}

dependencies {
    implementation(project(":components:bridge:api"))

    implementation(Libs.ANNOTATIONS)
    implementation(Libs.APPCOMPAT)
}

plugins {
    androidLibrary
}

dependencies {
    api(project(":components:bridge:api"))
    implementation(project(":components:bridge:impl"))
}

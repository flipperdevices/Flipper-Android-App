plugins {
    androidLibrary
}

dependencies {
    implementation(projects.components.bridge.api)

    implementation(Libs.ANNOTATIONS)
    implementation(Libs.APPCOMPAT)
}

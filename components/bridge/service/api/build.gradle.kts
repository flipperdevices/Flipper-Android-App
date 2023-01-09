plugins {
    id("flipper.android-lib")
}

dependencies {
    implementation(projects.components.bridge.api)

    implementation(libs.annotations)
    implementation(libs.appcompat)
}

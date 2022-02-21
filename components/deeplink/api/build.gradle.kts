plugins {
    androidLibrary
    id("kotlin-parcelize")
}

dependencies {
    implementation(projects.components.bridge.dao.api)

    implementation(libs.annotations)
    implementation(libs.appcompat)
}

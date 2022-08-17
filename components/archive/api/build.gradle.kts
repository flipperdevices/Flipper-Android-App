plugins {
    id("flipper.lint")
    id("flipper.android-lib")
    id("kotlin-parcelize")
}

dependencies {
    implementation(libs.cicerone)
    implementation(projects.components.bridge.dao.api)
}

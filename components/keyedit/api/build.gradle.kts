plugins {
    id("flipper.lint")
    id("flipper.android-lib")
    id("kotlin-parcelize")
}

dependencies {
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.core.preference)

    implementation(libs.cicerone)
}

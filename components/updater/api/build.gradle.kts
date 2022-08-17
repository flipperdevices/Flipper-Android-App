plugins {
    id("flipper.lint")
    id("flipper.android-compose")
    id("kotlin-parcelize")
}

dependencies {
    implementation(projects.components.bridge.service.api)

    implementation(libs.kotlin.coroutines)
    implementation(libs.compose.ui)

    implementation(libs.cicerone)
}

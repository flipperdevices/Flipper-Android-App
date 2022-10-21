plugins {
    id("flipper.lint")
    id("flipper.android-compose")
    id("kotlin-parcelize")
}

dependencies {
    implementation(projects.components.bridge.service.api)
    implementation(projects.components.deeplink.api)
    implementation(projects.components.core.ktx)

    implementation(libs.kotlin.coroutines)
    implementation(libs.compose.ui)

    implementation(libs.cicerone)
}

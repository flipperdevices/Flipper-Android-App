plugins {
    id("flipper.lint")
    id("flipper.android-compose")
    id("kotlinx-serialization")
    id("kotlin-parcelize")
}

dependencies {
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.res)

    implementation(libs.kotlin.coroutines)

    implementation(libs.cicerone)

    implementation(libs.kotlin.serialization.json)

    implementation(libs.compose.ui)
}

plugins {
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
    implementation(libs.kotlin.immutable.collections)

    implementation(libs.compose.ui)
}

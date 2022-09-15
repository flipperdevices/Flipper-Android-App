plugins {
    id("flipper.lint")
    id("flipper.android-compose")
    id("kotlin-parcelize")
}

dependencies {
    implementation(projects.components.core.ktx)

    implementation(libs.wear.gms)

    implementation(libs.compose.ui)
}

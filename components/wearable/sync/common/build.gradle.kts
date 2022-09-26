plugins {
    id("flipper.lint")
    id("flipper.android-compose")
    id("kotlin-parcelize")
    id("flipper.protobuf")
}

dependencies {
    implementation(projects.components.core.ktx)

    implementation(libs.wear.gms)

    implementation(libs.compose.ui)
}
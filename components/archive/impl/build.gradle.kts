plugins {
    androidCompose
    id("com.squareup.anvil")
    kotlin("kapt")
}

dependencies {
    implementation(project(":components:archive:api"))

    implementation(project(":components:core:di"))
    implementation(project(":components:core:ui"))

    implementation(project(":components:connection:api"))

    implementation(project(":components:bridge:dao"))

    implementation(Libs.ANNOTATIONS)
    implementation(Libs.APPCOMPAT)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(Libs.COMPOSE_PAGER)
    implementation(Libs.COMPOSE_PAGER_INDICATOR)

    // Dagger deps
    implementation(Libs.DAGGER)
    kapt(Libs.DAGGER_COMPILER)

    implementation(Libs.CICERONE)
}

plugins {
    androidCompose
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

dependencies {
    implementation(projects.components.firstpair.api)
    implementation(projects.components.core.di)
    implementation(projects.components.core.ui)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.navigation)

    implementation(projects.components.deeplink.api)
    implementation(projects.components.singleactivity.api)

    implementation(libs.cicerone)
    implementation(libs.appcompat)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)

    implementation(libs.ktx)
    implementation(libs.ktx.fragment)
}

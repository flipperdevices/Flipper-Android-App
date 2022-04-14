plugins {
    androidLibrary
    id("com.squareup.anvil")
    id("kotlin-kapt")
    id("kotlinx-serialization")
}

dependencies {
    implementation(projects.components.updater.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)

    implementation(libs.kotlin.serialization.json)

    implementation(libs.ktor.client)
    implementation(libs.ktor.serialization)
    implementation(libs.ktor.logging)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}

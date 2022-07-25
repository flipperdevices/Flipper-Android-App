plugins {
    androidLibrary
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

dependencies {
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.di)

    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}
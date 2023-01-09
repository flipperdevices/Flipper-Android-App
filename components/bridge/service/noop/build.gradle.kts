plugins {
    id("flipper.android-lib")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

dependencies {
    implementation(projects.components.bridge.service.api)
    implementation(projects.components.core.di)

    implementation(projects.components.bridge.api)

    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.runtime.ktx)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}

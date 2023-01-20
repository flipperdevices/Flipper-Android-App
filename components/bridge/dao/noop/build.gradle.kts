plugins {
    id("flipper.android-lib")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

dependencies {
    implementation(projects.components.bridge.dao.api)

    implementation(projects.components.core.di)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}

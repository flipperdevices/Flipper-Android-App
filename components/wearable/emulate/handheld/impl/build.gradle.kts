plugins {
    id("flipper.lint")
    id("flipper.android-lib")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

dependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.log)

    implementation(libs.wear)
    implementation(libs.wear.gms)

    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.service)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}

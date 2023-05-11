plugins {
    id("flipper.android-lib")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

android.namespace = "com.flipperdevices.selfupdater.unknown"

dependencies {
    implementation(projects.components.selfupdater.api)

    // Dagger deps
    implementation(projects.components.core.di)
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}

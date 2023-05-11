plugins {
    id("flipper.android-lib")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

android.namespace = "com.flipperdevices.wearable.sync.handheld.noop"

dependencies {
    implementation(projects.components.wearable.sync.handheld.api)

    implementation(projects.components.core.di)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}

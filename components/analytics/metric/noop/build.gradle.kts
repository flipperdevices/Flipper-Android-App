plugins {
    id("flipper.android-lib")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

android.namespace = "com.flipperdevices.metric.noop"

dependencies {
    implementation(projects.components.analytics.metric.api)

    implementation(projects.components.core.di)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}

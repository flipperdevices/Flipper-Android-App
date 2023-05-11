plugins {
    id("flipper.android-compose")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

android.namespace = "com.flipperdevices.shake2report.noop"

dependencies {
    implementation(projects.components.analytics.shake2report.api)
    implementation(projects.components.bridge.api)
    implementation(projects.components.core.di)
    implementation(projects.components.core.ui.navigation)

    implementation(libs.annotations)
    implementation(libs.appcompat)
    implementation(libs.compose.navigation)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}

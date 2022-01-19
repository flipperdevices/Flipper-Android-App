plugins {
    androidLibrary
    id("com.squareup.anvil")
    kotlin("kapt")
}

dependencies {
    implementation(projects.components.analytics.shake2report.api)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.di)

    implementation(libs.annotations)
    implementation(libs.appcompat)

    implementation(libs.kotlin.coroutines)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)

    implementation(libs.seismic)
    implementation(libs.timber)
    implementation(libs.timber.tressence)
    implementation(libs.sentry)
    implementation(libs.sentry.timber)
    implementation(libs.zip4j)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}

plugins {
    id("flipper.android-lib")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

android.buildFeatures.viewBinding = true

dependencies {
    implementation(projects.components.analytics.shake2report.api)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.di)
    implementation(projects.components.core.activityholder)
    implementation(projects.components.core.preference)

    implementation(projects.components.bridge.api)
    implementation(projects.components.info.api)

    implementation(libs.cicerone)
    implementation(libs.annotations)
    implementation(libs.appcompat)

    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.immutable.collections)
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

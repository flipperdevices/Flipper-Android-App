plugins {
    androidLibrary
    id("com.squareup.anvil")
    kotlin("kapt")
}

dependencies {
    implementation(projects.components.analytics.shake2report.api)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.di)

    implementation(Libs.ANNOTATIONS)
    implementation(Libs.APPCOMPAT)

    implementation(libs.kotlin.coroutines)
    implementation(Libs.LIFECYCLE_RUNTIME_KTX)
    implementation(Libs.LIFECYCLE_VIEWMODEL_KTX)

    implementation(Libs.SEISMIC)
    implementation(Libs.TIMBER)
    implementation(Libs.TREESSENCE)
    implementation(Libs.SENTRY)
    implementation(Libs.SENTRY_TIMBER)
    implementation(Libs.ZIP4J)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}

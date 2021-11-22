plugins {
    id("com.android.library")
    id("kotlin-android")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}
apply<com.flipperdevices.gradle.ConfigurationPlugin>()
apply<com.flipperdevices.gradle.ComposerPlugin>()

dependencies {
    implementation(project(":components:connection:api"))

    implementation(project(":components:core:di"))
    implementation(project(":components:core:ui"))

    // Compose
    implementation(Libs.COMPOSE_UI)
    implementation(Libs.COMPOSE_TOOLING)
    implementation(Libs.COMPOSE_FOUNDATION)
    implementation(Libs.COMPOSE_MATERIAL)

    // Dagger deps
    implementation(Libs.DAGGER)
    kapt(Libs.DAGGER_COMPILER)

    implementation(Libs.LIFECYCLE_RUNTIME_KTX)
    implementation(Libs.LIFECYCLE_VIEWMODEL_KTX)
    implementation(Libs.FRAGMENT_KTX)
}

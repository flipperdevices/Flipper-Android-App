plugins {
    id("kotlin-kapt")
    id("com.android.library")
    id("com.squareup.anvil")
    id("kotlin-android")
}

apply<com.flipper.gradle.ConfigurationPlugin>()
apply<com.flipper.gradle.ComposerPlugin>()

dependencies {
    implementation(project(":components:core"))
    implementation(project(":components:pair:api"))

    implementation(Libs.APPCOMPAT)
    implementation(Libs.DAGGER)
    kapt(Libs.DAGGER_COMPILER)

    implementation(Libs.COMPOSE_UI)
    implementation(Libs.COMPOSE_MATERIAL)
    implementation(Libs.COMPOSE_TOOLING)
    implementation(Libs.COMPOSE_FOUNDATION)

    implementation(Libs.KOTLIN_COROUTINES)
    implementation(Libs.LIFECYCLE_RUNTIME_KTX)
    implementation(Libs.LIFECYCLE_VIEWMODEL_KTX)
    implementation(Libs.ACTIVITY_KTX)
}

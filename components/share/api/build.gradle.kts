plugins {
    id("com.android.library")
    id("kotlin-android")
}

apply<com.flipperdevices.gradle.ConfigurationPlugin>()
apply<com.flipperdevices.gradle.ComposerPlugin>()

dependencies {
    implementation(project(":components:deeplink:api"))

    implementation(Libs.COMPOSE_UI)
}

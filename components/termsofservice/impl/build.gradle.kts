plugins {
    id("com.android.library")
    id("com.squareup.anvil")
    id("kotlin-android")
    id("kotlin-kapt")
}
apply<com.flipper.gradle.ConfigurationPlugin>()
apply<com.flipper.gradle.ComposerPlugin>()

dependencies {
    implementation(project(":components:core"))
    implementation(project(":components:termsofservice:api"))

    implementation(Libs.ANNOTATIONS)
    implementation(Libs.APPCOMPAT)
    implementation(Libs.COMPOSE_UI)
    implementation(Libs.COMPOSE_TOOLING)
    implementation(Libs.COMPOSE_FOUNDATION)
    implementation(Libs.COMPOSE_MATERIAL)

    implementation(Libs.DAGGER)
    kapt(Libs.DAGGER_COMPILER)
}

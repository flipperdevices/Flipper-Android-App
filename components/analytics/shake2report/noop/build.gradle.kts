plugins {
    id("com.android.library")
    id("kotlin-android")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}
apply<com.flipperdevices.gradle.ConfigurationPlugin>()

dependencies {
    implementation(project(":components:analytics:shake2report:api"))
    implementation(project(":components:core:di"))

    implementation(Libs.ANNOTATIONS)
    implementation(Libs.APPCOMPAT)

    // Dagger deps
    implementation(Libs.DAGGER)
    kapt(Libs.DAGGER_COMPILER)
}

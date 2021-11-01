plugins {
    id("com.android.library")
    id("kotlin-android")
}
apply<com.flipperdevices.gradle.ConfigurationPlugin>()

dependencies {
    implementation(project(":components:bridge:api"))

    implementation(Libs.ANNOTATIONS)
    implementation(Libs.APPCOMPAT)
}

plugins {
    id("com.android.library")
    id("kotlin-android")
}
apply<com.flipperdevices.gradle.ConfigurationPlugin>()

dependencies {
    api(project(":components:bridge:api"))
    implementation(project(":components:bridge:impl"))
}

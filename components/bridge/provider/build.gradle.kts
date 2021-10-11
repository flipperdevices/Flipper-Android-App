plugins {
    id("com.android.library")
    id("kotlin-android")
}
apply<com.flipper.gradle.ConfigurationPlugin>()

dependencies {
    api(project(":components:bridge:api"))
    implementation(project(":components:bridge:impl"))
}

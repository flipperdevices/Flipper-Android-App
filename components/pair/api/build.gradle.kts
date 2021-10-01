plugins {
    id("com.android.library")
    id("kotlin-android")
}
apply<com.flipper.gradle.ConfigurationPlugin>()

dependencies {
    implementation(project(":components:core"))

    implementation(Libs.ANNOTATIONS)
    implementation(Libs.APPCOMPAT)
}

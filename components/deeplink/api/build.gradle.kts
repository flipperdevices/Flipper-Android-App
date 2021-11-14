plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-parcelize")
}
apply<com.flipperdevices.gradle.ConfigurationPlugin>()

dependencies {

    implementation(Libs.ANNOTATIONS)
    implementation(Libs.APPCOMPAT)
}

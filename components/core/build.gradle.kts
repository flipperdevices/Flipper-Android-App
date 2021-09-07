plugins {
    id("com.android.library")
    id("com.squareup.anvil")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
}

apply<com.flipper.gradle.ConfigurationPlugin>()
apply<com.flipper.gradle.ComposerPlugin>()

android {
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

dependencies {
    api(Libs.TIMBER)
    api(Libs.KOTLIN)

    implementation(Libs.ANNOTATIONS)
    implementation(Libs.CORE_KTX)
    implementation(Libs.APPCOMPAT)

    api(Libs.CICERONE)
    implementation(Libs.COMPOSE_UI)
    implementation(Libs.COMPOSE_MATERIAL)

    implementation(Libs.DAGGER)
    kapt(Libs.DAGGER_COMPILER)
}

plugins {
    id("com.android.library")
    id("com.squareup.anvil")
    id("kotlin-android")
    id("kotlin-kapt")
}

apply<com.flipperdevices.gradle.ConfigurationPlugin>()
apply<com.flipperdevices.gradle.ComposerPlugin>()

android {
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

dependencies {
    implementation(project(":components:core:ui"))
    implementation(project(":components:core:di"))
    implementation(project(":components:core:ktx"))
    implementation(project(":components:core:log"))
    implementation(project(":components:core:preference"))
    implementation(project(":components:core:navigation"))

    implementation(project(":components:bottombar:api"))

    implementation(project(":components:singleactivity:api"))

    implementation(project(":components:bridge:api"))
    implementation(project(":components:bridge:provider"))
    implementation(project(":components:bridge:service:api"))
    implementation(project(":components:pair:api"))

    implementation(Libs.KOTLIN_COROUTINES)
    implementation(Libs.LIFECYCLE_RUNTIME_KTX)
    implementation(Libs.LIFECYCLE_VIEWMODEL_KTX)
    implementation(Libs.ANNOTATIONS)
    implementation(Libs.CORE_KTX)
    implementation(Libs.APPCOMPAT)
    implementation(Libs.DAGGER)
    kapt(Libs.DAGGER_COMPILER)
    implementation(Libs.COMPOSE_UI)
    implementation(Libs.COMPOSE_TOOLING)
    implementation(Libs.COMPOSE_FOUNDATION)
    implementation(Libs.COMPOSE_MATERIAL)
    implementation(Libs.FRAGMENT_KTX)

    // Guide
    implementation(Libs.IMAGE_SLIDER)
    implementation(Libs.GLIDE)
    implementation(Libs.LOTTIE)

    implementation(Libs.NORDIC_BLE)
    implementation(Libs.NORDIC_BLE_KTX)
    implementation(Libs.NORDIC_BLE_COMMON)
    implementation(Libs.NORDIC_BLE_SCAN)

    implementation(Libs.CICERONE)
}

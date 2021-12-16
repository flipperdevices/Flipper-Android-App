plugins {
    androidCompose
    id("com.squareup.anvil")
    kotlin("kapt")
}

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

    implementation(project(":components:deeplink:api"))

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
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
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

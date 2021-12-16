plugins {
    androidCompose
    id("com.squareup.anvil")
    kotlin("kapt")
}

dependencies {
    implementation(project(":components:core:di"))
    implementation(project(":components:core:ui"))
    implementation(project(":components:core:navigation"))

    implementation(project(":components:info:api"))
    implementation(project(":components:pair:api"))
    implementation(project(":components:screenstreaming:api"))
    implementation(project(":components:filemanager:api"))
    implementation(project(":components:debug:api"))

    implementation(project(":components:bridge:api"))
    implementation(project(":components:bridge:service:api"))
    implementation(project(":components:bridge:protobuf"))

    // Core deps
    implementation(Libs.CORE_KTX)
    implementation(Libs.ANNOTATIONS)

    implementation(Libs.APPCOMPAT)
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)

    implementation(libs.compose.ui)
    implementation(libs.compose.material)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)

    implementation(Libs.KOTLIN_COROUTINES)
    implementation(Libs.LIFECYCLE_RUNTIME_KTX)
    implementation(Libs.LIFECYCLE_VIEWMODEL_KTX)

    implementation(Libs.FRAGMENT_KTX)

    implementation(Libs.NORDIC_BLE)
    implementation(Libs.NORDIC_BLE_KTX)
    implementation(Libs.NORDIC_BLE_COMMON)
    implementation(Libs.NORDIC_BLE_SCAN)

    implementation(Libs.CICERONE)
}

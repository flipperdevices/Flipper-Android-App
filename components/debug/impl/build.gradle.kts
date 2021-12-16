plugins {
    androidCompose
    id("com.squareup.anvil")
    kotlin("kapt")
}

dependencies {
    implementation(project(":components:debug:api"))

    implementation(project(":components:core:di"))
    implementation(project(":components:core:ui"))
    implementation(project(":components:core:log"))
    implementation(project(":components:core:ktx"))
    implementation(project(":components:core:navigation"))

    implementation(project(":components:bridge:api"))
    implementation(project(":components:bridge:protobuf"))
    implementation(project(":components:bridge:service:api"))

    implementation(project(":components:bridge:synchronization:api"))

    implementation(Libs.NORDIC_BLE_KTX)

    implementation(Libs.ANNOTATIONS)
    implementation(Libs.APPCOMPAT)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)

    implementation(Libs.LIFECYCLE_RUNTIME_KTX)
    implementation(Libs.LIFECYCLE_VIEWMODEL_KTX)
    implementation(Libs.LIFECYCLE_COMPOSE)
    implementation(Libs.FRAGMENT_KTX)

    implementation(Libs.CICERONE)
}

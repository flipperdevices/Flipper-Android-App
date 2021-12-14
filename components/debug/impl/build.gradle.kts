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

    implementation(project(":components:bridge:api"))
    implementation(project(":components:bridge:protobuf"))
    implementation(project(":components:bridge:service:api"))
    implementation(Libs.NORDIC_BLE_KTX)

    implementation(Libs.ANNOTATIONS)
    implementation(Libs.APPCOMPAT)

    // Compose
    implementation(Libs.COMPOSE_UI)
    implementation(Libs.COMPOSE_TOOLING)
    implementation(Libs.COMPOSE_FOUNDATION)
    implementation(Libs.COMPOSE_MATERIAL)

    // Dagger deps
    implementation(Libs.DAGGER)
    kapt(Libs.DAGGER_COMPILER)

    implementation(Libs.LIFECYCLE_RUNTIME_KTX)
    implementation(Libs.LIFECYCLE_VIEWMODEL_KTX)
    implementation(Libs.LIFECYCLE_COMPOSE)
    implementation(Libs.FRAGMENT_KTX)

    implementation(Libs.CICERONE)
}

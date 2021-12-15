plugins {
    androidCompose
    id("com.squareup.anvil")
    kotlin("kapt")
}

dependencies {
    implementation(project(":components:bridge:protobuf"))
    implementation(project(":components:bridge:api"))
    implementation(project(":components:bridge:service:api"))
    implementation(project(":components:screenstreaming:api"))

    implementation(project(":components:core:di"))
    implementation(project(":components:core:ui"))
    implementation(project(":components:core:log"))

    implementation(Libs.ANNOTATIONS)
    implementation(Libs.APPCOMPAT)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(Libs.COMPOSE_CONSTRAINT_LAYOUT)

    implementation(Libs.KOTLIN_COROUTINES)
    implementation(Libs.LIFECYCLE_RUNTIME_KTX)
    implementation(Libs.LIFECYCLE_VIEWMODEL_KTX)
    implementation(Libs.LIFECYCLE_COMPOSE)
    implementation(Libs.FRAGMENT_KTX)

    implementation(Libs.CICERONE)

    // Dagger deps
    implementation(Libs.DAGGER)
    kapt(Libs.DAGGER_COMPILER)
}

plugins {
    androidCompose
    id("com.squareup.anvil")
    kotlin("kapt")
}

dependencies {
    implementation(project(":components:core:di"))
    implementation(project(":components:core:ui"))
    implementation(project(":components:core:log"))
    implementation(project(":components:core:ktx"))

    implementation(project(":components:deeplink:api"))

    implementation(project(":components:share:api"))
    implementation(project(":components:share:common"))

    implementation(project(":components:bridge:api"))
    implementation(project(":components:bridge:protobuf"))
    implementation(project(":components:bridge:service:api"))

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

    // Lifecycle
    implementation(Libs.LIFECYCLE_RUNTIME_KTX)
    implementation(Libs.LIFECYCLE_VIEWMODEL_KTX)
    implementation(Libs.LIFECYCLE_COMPOSE)

    implementation(Libs.KOTLIN_COROUTINES)
}

plugins {
    androidCompose
    id("com.squareup.anvil")
    kotlin("kapt")
}

dependencies {
    implementation(project(":components:bottombar:api"))

    implementation(project(":components:core:di"))
    implementation(project(":components:core:navigation"))
    implementation(project(":components:core:ui"))

    implementation(project(":components:info:api"))
    implementation(project(":components:archive:api"))
    implementation(project(":components:pair:api"))

    implementation(Libs.APPCOMPAT)
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)

    implementation(libs.compose.ui)
    implementation(libs.compose.material)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(Libs.COMPOSE_PAGER)
    implementation(Libs.COMPOSE_SYSTEM_UI_CONTROLLER)

    implementation(Libs.KOTLIN_COROUTINES)
    implementation(Libs.LIFECYCLE_RUNTIME_KTX)
    implementation(Libs.LIFECYCLE_VIEWMODEL_KTX)
    implementation(Libs.FRAGMENT_KTX)

    implementation(Libs.CICERONE)
}

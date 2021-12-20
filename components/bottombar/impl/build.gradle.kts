plugins {
    androidCompose
    id("com.squareup.anvil")
    kotlin("kapt")
}

dependencies {
    implementation(projects.components.bottombar.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.navigation)
    implementation(projects.components.core.ui)

    implementation(projects.components.info.api)
    implementation(projects.components.archive.api)
    implementation(projects.components.pair.api)

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

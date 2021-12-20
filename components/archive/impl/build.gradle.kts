plugins {
    androidCompose
    id("com.squareup.anvil")
    kotlin("kapt")
}

dependencies {
    implementation(projects.components.archive.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.ui)

    implementation(projects.components.connection.api)

    implementation(projects.components.bridge.dao.api)

    implementation(Libs.ANNOTATIONS)
    implementation(Libs.APPCOMPAT)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(Libs.COMPOSE_PAGER)
    implementation(Libs.COMPOSE_PAGER_INDICATOR)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)

    implementation(Libs.CICERONE)

    // Lifecycle
    implementation(Libs.LIFECYCLE_RUNTIME_KTX)
    implementation(Libs.LIFECYCLE_VIEWMODEL_KTX)
    implementation(Libs.LIFECYCLE_COMPOSE)
}

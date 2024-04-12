plugins {
    id("flipper.android-compose")
}

android.namespace = "com.flipperdevices.keyemulate.api"

dependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.service.api)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
    implementation(libs.decompose)

    // Dagger deps
    implementation(libs.dagger)
}

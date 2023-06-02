plugins {
    id("flipper.android-compose")
    id("com.squareup.anvil")
    id("kotlin-kapt")
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

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}

plugins {
    androidCompose
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

dependencies {
    implementation(projects.components.archive.api)
    implementation(projects.components.archive.shared)

    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.keyscreen.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.ui)

    implementation(libs.cicerone)
    implementation(libs.appcompat)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.systemui)

    implementation(libs.lifecycle.compose)
    implementation(libs.lifecycle.viewmodel.ktx)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}

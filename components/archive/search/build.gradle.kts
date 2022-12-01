plugins {
    id("flipper.lint")
    id("flipper.android-compose")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

dependencies {
    implementation(projects.components.archive.api)
    implementation(projects.components.archive.shared)

    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.synchronization.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.searchbar)
    implementation(projects.components.core.ui.fragment)
    implementation(projects.components.core.ui.theme)

    implementation(libs.cicerone)
    implementation(libs.appcompat)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)

    implementation(libs.lifecycle.compose)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.ktx.fragment)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}

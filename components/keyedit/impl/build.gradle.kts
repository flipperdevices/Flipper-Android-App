plugins {
    androidCompose
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

dependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.ui)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.navigation)

    implementation(projects.components.keyedit.api)

    implementation(projects.components.keyscreen.shared)

    implementation(projects.components.bridge.dao.api)

    implementation(libs.cicerone)
    implementation(libs.appcompat)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)

    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.ktx.fragment)
}

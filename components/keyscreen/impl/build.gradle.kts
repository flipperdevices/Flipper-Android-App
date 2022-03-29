plugins {
    androidCompose
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

dependencies {
    implementation(projects.components.keyscreen.api)
    implementation(projects.components.keyscreen.shared)

    implementation(projects.components.core.di)
    implementation(projects.components.core.ui)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.navigation)
    implementation(projects.components.core.preference)
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.synchronization.api)

    implementation(projects.components.keyedit.api)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)

    implementation(libs.cicerone)
    implementation(libs.appcompat)

    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.ktx.fragment)
}

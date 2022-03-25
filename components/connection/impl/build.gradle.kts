plugins {
    androidCompose
    id("com.squareup.anvil")
    kotlin("kapt")
}

dependencies {
    implementation(projects.components.connection.api)
    implementation(projects.components.bottombar.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.ui)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.activityholder)

    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.service.api)
    implementation(projects.components.bridge.synchronization.api)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.image.lottie)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)

    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.compose)
    implementation(libs.ktx.fragment)
}

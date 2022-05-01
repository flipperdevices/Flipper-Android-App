plugins {
    androidCompose
    id("com.squareup.anvil")
    kotlin("kapt")
}

dependencies {
    implementation(projects.components.bridge.protobuf)
    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.service.api)
    implementation(projects.components.screenstreaming.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.ui)
    implementation(projects.components.core.log)

    implementation(libs.annotations)
    implementation(libs.appcompat)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.constraint)

    implementation(libs.kotlin.coroutines)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.compose)
    implementation(libs.ktx.fragment)

    implementation(libs.cicerone)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)

    // Protobuf jvm
    implementation(libs.protobuf.jvm)
}

plugins {
    androidLibrary
    androidCompose
    id("com.squareup.anvil")
    id("kotlin-kapt")
    id("kotlinx-serialization")
}

dependencies {
    implementation(projects.components.bridge.synchronization.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.ui)
    implementation(projects.components.core.log)

    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.protobuf)
    implementation(projects.components.bridge.service.api)
    implementation(projects.components.bridge.dao)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)

    // Dagger deps
    implementation(Libs.DAGGER)
    kapt(Libs.DAGGER_COMPILER)

    implementation(Libs.LIFECYCLE_RUNTIME_KTX)

    implementation(libs.kotlin.serialization.json)
}

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
    implementation(Libs.COMPOSE_UI)
    implementation(Libs.COMPOSE_TOOLING)
    implementation(Libs.COMPOSE_FOUNDATION)
    implementation(Libs.COMPOSE_MATERIAL)

    // Dagger deps
    implementation(Libs.DAGGER)
    kapt(Libs.DAGGER_COMPILER)

    implementation(Libs.LIFECYCLE_RUNTIME_KTX)

    implementation(libs.kotlin.serialization.json)
}

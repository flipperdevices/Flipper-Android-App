plugins {
    androidCompose
    id("com.squareup.anvil")
    kotlin("kapt")
}

dependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.ui)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)

    implementation(projects.components.deeplink.api)

    implementation(projects.components.filemanager.api)
    implementation(projects.components.filemanager.sharecommon)

    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.protobuf)
    implementation(projects.components.bridge.service.api)

    implementation(libs.annotations)
    implementation(libs.appcompat)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)

    // Lifecycle
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.compose)

    implementation(libs.kotlin.coroutines)

    // Protobuf jvm
    implementation(libs.protobuf.jvm)
}

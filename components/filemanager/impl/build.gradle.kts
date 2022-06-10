plugins {
    androidCompose
    id("com.squareup.anvil")
    kotlin("kapt")
}

dependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.navigation)
    implementation(projects.components.core.ui.fragment)
    implementation(projects.components.core.ui.lifecycle)

    implementation(projects.components.bridge.service.api)
    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.pbutils)
    implementation(libs.protobuf.jvm)

    implementation(projects.components.filemanager.api)

    implementation(projects.components.deeplink.api)

    implementation(projects.components.bottombar.api)

    implementation(libs.annotations)
    implementation(libs.appcompat)

    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)

    implementation(libs.kotlin.coroutines)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.ktx.fragment)

    implementation(libs.dagger)
    kapt(libs.dagger.kapt)

    implementation(libs.cicerone)
}

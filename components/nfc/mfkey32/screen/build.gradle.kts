plugins {
    id("flipper.lint")
    id("flipper.android-compose")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

dependencies {
    implementation(projects.components.nfc.mfkey32.api)
    implementation(projects.components.nfc.tools.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.ui.navigation)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.core.markdown)

    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.service.api)
    implementation(projects.components.bridge.pbutils)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.navigation)

    implementation(libs.protobuf.jvm)

    implementation(libs.lifecycle.viewmodel.ktx)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
    implementation(libs.tangle.viewmodel.compose)
    implementation(libs.tangle.viewmodel.api)
    anvil(libs.tangle.viewmodel.compiler)
}

plugins {
    androidCompose
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

dependencies {
    implementation(projects.components.nfceditor.api)

    implementation(projects.components.core.keyinputbus)
    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.navigation)
    implementation(projects.components.core.log)
    implementation(projects.components.core.di)

    implementation(projects.components.bridge.dao.api)

    implementation(libs.cicerone)
    implementation(libs.appcompat)
    implementation(projects.components.core.ui.fragment)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.insets)

    implementation(libs.lifecycle.compose)
    implementation(libs.ktx.fragment)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)

    // Testing
    testImplementation(projects.components.core.test)
    testImplementation(libs.junit)
    testImplementation(libs.mockito.kotlin)
}

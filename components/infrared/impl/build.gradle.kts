plugins {
    id("flipper.android-compose")
    id("kotlinx-serialization")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

dependencies {
    // Api
    implementation(projects.components.infrared.api)
    implementation(projects.components.share.api)
    implementation(projects.components.keyedit.api)
    implementation(projects.components.keyscreen.api)
    implementation(projects.components.keyscreen.shared)
    implementation(projects.components.bridge.dao.api)

    // Core
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.theme)

    // Navigation
    implementation(libs.compose.navigation)
    implementation(projects.components.core.ui.navigation)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.navigation)
    implementation(libs.compose.navigation.material)

    implementation(libs.kotlin.serialization.json)
    implementation(libs.kotlin.immutable.collections)

    implementation(projects.components.core.log)

    // DI
    implementation(projects.components.core.di)
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
    implementation(libs.tangle.viewmodel.compose)
    implementation(libs.tangle.viewmodel.api)
    anvil(libs.tangle.viewmodel.compiler)
}

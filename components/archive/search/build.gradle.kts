plugins {
    id("flipper.android-compose")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

dependencies {
    implementation(projects.components.archive.api)
    implementation(projects.components.archive.shared)
    implementation(projects.components.keyscreen.api)

    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.synchronization.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.searchbar)
    implementation(projects.components.core.ui.fragment)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.navigation)

    implementation(libs.cicerone)
    implementation(libs.appcompat)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.navigation)

    implementation(libs.lifecycle.compose)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.ktx.fragment)

    implementation(libs.kotlin.immutable.collections)

    // Dagger deps
    implementation(libs.dagger)
    implementation(project(mapOf("path" to ":components:keyscreen:api")))
    kapt(libs.dagger.kapt)
    implementation(libs.tangle.viewmodel.compose)
    implementation(libs.tangle.viewmodel.api)
    anvil(libs.tangle.viewmodel.compiler)
}

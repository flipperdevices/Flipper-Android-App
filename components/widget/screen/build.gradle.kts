plugins {
    id("flipper.lint")
    id("flipper.android-compose")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

dependencies {
    implementation(projects.components.widget.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.navigation)
    implementation(projects.components.core.activityholder)
    implementation(projects.components.core.ui.fragment)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.res)

    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.synchronization.api)
    implementation(projects.components.bridge.dao.api)

    implementation(libs.cicerone)
    implementation(libs.appcompat)

    implementation(projects.components.deeplink.api)
    implementation(projects.components.archive.api)
    implementation(projects.components.archive.shared)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.swipetorefresh)

    implementation(libs.lifecycle.viewmodel.ktx)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)

    implementation(libs.tangle.viewmodel.fragment)
    implementation(libs.tangle.viewmodel.api)
    anvil(libs.tangle.viewmodel.compiler)
}

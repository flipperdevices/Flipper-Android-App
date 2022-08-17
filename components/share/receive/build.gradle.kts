plugins {
    id("flipper.lint")
    id("flipper.android-compose")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

dependencies {
    implementation(projects.components.deeplink.api)
    implementation(projects.components.share.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.navigation)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.fragment)
    implementation(projects.components.core.ui.theme)

    implementation(projects.components.analytics.metric.api)

    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.synchronization.api)

    implementation(projects.components.keyscreen.api)
    implementation(projects.components.keyscreen.shared)
    implementation(projects.components.keyedit.api)

    implementation(projects.components.inappnotification.api)

    implementation(libs.cicerone)
    implementation(libs.appcompat)

    implementation(libs.kotlin.coroutines)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.compose)
    implementation(libs.ktx.fragment)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}

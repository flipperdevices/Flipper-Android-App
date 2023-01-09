plugins {
    id("flipper.android-compose")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

dependencies {
    implementation(projects.components.updater.api)
    implementation(projects.components.info.shared)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.navigation)
    implementation(projects.components.core.markdown)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.activityholder)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.fragment)
    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.dialog)

    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.service.api)
    implementation(projects.components.bridge.synchronization.api)
    implementation(projects.components.singleactivity.api)
    implementation(projects.components.deeplink.api)
    implementation(projects.components.analytics.metric.api)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)

    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.compose)
    implementation(libs.ktx.fragment)

    implementation(libs.cicerone)

    implementation(libs.appcompat)
    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}

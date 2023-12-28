plugins {
    id("flipper.android-compose")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.singleactivity.impl"

dependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.theme)

    implementation(projects.components.deeplink.api)
    implementation(projects.components.singleactivity.api)
    implementation(projects.components.selfupdater.api)
    implementation(projects.components.unhandledexception.api)
    implementation(projects.components.rootscreen.api)

    implementation(projects.components.analytics.metric.api)
    implementation(projects.components.bridge.synchronization.api)

    implementation(libs.annotations)
    implementation(libs.appcompat)

    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.bundles.decompose)
    implementation(libs.compose.activity)

    implementation(libs.ktx.activity)
    implementation(libs.kotlin.immutable.collections)
}

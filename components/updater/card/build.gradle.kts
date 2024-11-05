plugins {
    id("flipper.android-compose")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.updater.card"

dependencies {
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.log)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.di)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.core.ui.dialog)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.deeplink.api)
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.synchronization.api)
    implementation(projects.components.core.ui.decompose)

    implementation(projects.components.updater.api)
    implementation(projects.components.updater.subghz)
    implementation(projects.components.info.shared)
    implementation(projects.components.rootscreen.api)

    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.rpcinfo.api)
    implementation(projects.components.bridge.service.api)
    implementation(projects.components.bridge.pbutils)

    implementation(projects.components.analytics.metric.api)

    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.compose)

    implementation(libs.annotations)
    implementation(libs.appcompat)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.activity)
    implementation(libs.decompose)

    // Testing
    testImplementation(projects.components.core.buildKonfig)
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.roboelectric)
    testImplementation(libs.ktx.testing)
    testImplementation(libs.mockk)
}

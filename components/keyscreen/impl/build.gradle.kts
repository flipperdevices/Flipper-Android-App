plugins {
    id("flipper.android-compose")
    id("flipper.anvil")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.keyscreen.impl"

dependencies {
    implementation(projects.components.keyscreen.api)
    implementation(projects.components.share.api)
    implementation(projects.components.keyscreen.shared)
    implementation(projects.components.archive.api)
    implementation(projects.components.keyemulate.api)
    implementation(projects.components.keyparser.api)
    implementation(projects.components.infrared.api)

    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.navigation)
    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.core.preference)

    implementation(projects.components.analytics.metric.api)

    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.service.api)
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.pbutils)
    implementation(projects.components.bridge.synchronization.api)

    implementation(projects.components.keyedit.api)
    implementation(projects.components.nfceditor.api)
    implementation(projects.components.deeplink.api)
    implementation(projects.components.bottombar.api)

    implementation(libs.kotlin.serialization.json)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.navigation)
    implementation(libs.compose.navigation.material)
    implementation(libs.image.lottie)

    implementation(libs.tangle.viewmodel.compose)
    implementation(libs.tangle.viewmodel.api)
    anvil(libs.tangle.viewmodel.compiler)

    implementation(libs.appcompat)

    implementation(libs.lifecycle.compose)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.ktx.fragment)

    // Testing
    testImplementation(projects.components.core.test)
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.roboelectric)
    testImplementation(libs.ktx.testing)
    testImplementation(libs.mockk)
}

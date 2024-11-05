plugins {
    id("flipper.android-compose")
    id("flipper.anvil")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.nfceditor.impl"

dependencies {
    implementation(projects.components.nfceditor.api)

    implementation(projects.components.core.ui.lifecycle)

    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.dialog)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.core.ui.hexkeyboard)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.log)
    implementation(projects.components.core.di)
    implementation(projects.components.core.data)

    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.synchronization.api)

    implementation(projects.components.keyparser.api)
    implementation(projects.components.keyedit.api)
    implementation(projects.components.keyscreen.shared)

    implementation(projects.components.analytics.metric.api)

    implementation(libs.appcompat)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.bundles.decompose)

    implementation(libs.kotlin.immutable.collections)
    implementation(libs.kotlin.serialization.json)

    implementation(libs.lifecycle.compose)

    // Testing
    testImplementation(projects.components.core.test)
    testImplementation(projects.components.core.buildKonfig)
    testImplementation(libs.roboelectric)
    testImplementation(libs.junit)
    testImplementation(libs.ktx.testing)
    testImplementation(libs.mockito.kotlin)
    testImplementation(projects.components.keyparser.impl)
}

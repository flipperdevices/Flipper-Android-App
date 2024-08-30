plugins {
    id("flipper.android-lib")
    id("flipper.android-compose")
    id("flipper.anvil")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.bridge.synchronization.impl"

dependencies {
    implementation(projects.components.bridge.synchronization.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.log)
    implementation(projects.components.core.data)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.progress)
    implementation(projects.components.core.ui.lifecycle)

    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.pbutils)
    implementation(projects.components.bridge.service.api)
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.rpc.api)

    implementation(projects.components.wearable.sync.handheld.api)
    implementation(projects.components.nfc.mfkey32.api)

    implementation(projects.components.analytics.shake2report.api)
    implementation(projects.components.analytics.metric.api)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)

    implementation(libs.lifecycle.runtime.ktx)

    implementation(libs.kotlin.serialization.json)
    implementation(libs.kotlin.immutable.collections)

    // Dagger deps
    implementation(libs.dagger)

    // Testing
    testImplementation(projects.components.core.test)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.ktx.testing)
    testImplementation(libs.roboelectric)
    testImplementation(libs.lifecycle.test)
    testImplementation(libs.kotlin.coroutines.test)
}

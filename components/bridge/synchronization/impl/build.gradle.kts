plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil-multiplatform")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.bridge.synchronization.impl"

commonDependencies {
    implementation(projects.components.bridge.synchronization.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.log)
    implementation(projects.components.core.data)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.progress)
    implementation(projects.components.core.atomicfile)
    implementation(projects.components.core.storage)
    implementation(projects.components.core.ui.lifecycle)

    implementation(projects.components.bridge.dao.api)

    implementation(projects.components.bridge.connection.feature.common.api)
    implementation(projects.components.bridge.connection.feature.provider.api)
    implementation(projects.components.bridge.connection.feature.storage.api)

    implementation(projects.components.wearable.sync.handheld.api)
    implementation(projects.components.nfc.mfkey32.api)

    implementation(projects.components.analytics.shake2report.api)
    implementation(projects.components.analytics.metric.api)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)

    implementation(libs.kotlin.serialization.json)
    implementation(libs.kotlin.serialization.json.okio)
    implementation(libs.kotlin.immutable.collections)
    implementation(libs.okio)

    // Dagger deps
    implementation(libs.dagger)
}

jvmSharedTestDependencies {
    implementation(projects.components.core.test)
    implementation(libs.junit)
    implementation(libs.mockk)
    implementation(libs.roboelectric)
    implementation(libs.kotlin.coroutines.test)
}

plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
    id("kotlinx-serialization")
}


androidDependencies {
    implementation(projects.components.core.buildKonfig)
    implementation(projects.components.bridge.synchronization.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.log)
    implementation(projects.components.core.data)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.progress)
    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.core.storage)

    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.connection.feature.storage.api)
    implementation(projects.components.bridge.connection.feature.common.api)
    implementation(projects.components.bridge.connection.feature.provider.api)

    implementation(projects.components.wearable.sync.handheld.api)
    implementation(projects.components.nfc.mfkey32.api)

    implementation(projects.components.analytics.shake2report.api)
    implementation(projects.components.analytics.metric.api)

    // Compose

    implementation(libs.lifecycle.runtime.ktx)

    implementation(libs.kotlin.serialization.json)
    implementation(libs.kotlin.immutable.collections)

    // Dagger deps
    // Testing
}

androidHostTestDependencies {
    implementation(projects.components.core.test)
    implementation(projects.components.core.buildKonfig)
    implementation(libs.junit)
    implementation(libs.mockk)
    implementation(libs.ktx.testing)
    implementation(libs.roboelectric)
    implementation(libs.lifecycle.test)
    implementation(libs.kotlin.coroutines.test)
}

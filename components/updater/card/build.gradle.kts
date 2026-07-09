plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
}

androidDependencies {
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

    implementation(projects.components.analytics.metric.api)

    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.compose)

    implementation(libs.annotations)
    implementation(libs.appcompat)

    implementation(projects.components.bridge.connection.feature.provider.api)
    implementation(projects.components.bridge.connection.feature.common.api)
    implementation(projects.components.bridge.connection.feature.update.api)
    implementation(projects.components.bridge.connection.orchestrator.api)
    implementation(projects.components.bridge.connection.feature.protocolversion.api)
    implementation(projects.components.bridge.connection.feature.storage.api)
    implementation(projects.components.bridge.connection.feature.storageinfo.api)
    implementation(projects.components.bridge.connection.feature.getinfo.api)
    implementation(projects.components.bridge.connection.feature.rpcinfo.api)
    implementation(projects.components.bridge.connection.pbutils)

    // Compose
    implementation(libs.compose.activity)
    implementation(libs.decompose)

    // Testing
}

androidHostTestDependencies {
    implementation(projects.components.core.buildKonfig)
    implementation(libs.junit)
    implementation(libs.kotlin.coroutines.test)
    implementation(libs.roboelectric)
    implementation(libs.ktx.testing)
    implementation(libs.mockk)
}

plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
}


androidDependencies {
    implementation(projects.components.updater.api)
    implementation(projects.components.updater.subghz)

    implementation(projects.components.deeplink.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.storage)
    implementation(projects.components.core.progress)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.core.ui.res)

    implementation(projects.components.analytics.metric.api)
    implementation(projects.components.faphub.installedtab.api)

    implementation(projects.components.bridge.connection.feature.provider.api)
    implementation(projects.components.bridge.connection.feature.common.api)
    implementation(projects.components.bridge.connection.feature.storage.api)
    implementation(projects.components.bridge.connection.feature.getinfo.api)
    implementation(projects.components.bridge.connection.feature.update.api)
    implementation(projects.components.bridge.connection.feature.rpc.api)
    implementation(projects.components.bridge.connection.pbutils)
    implementation(projects.components.bridge.connection.orchestrator.api)

    implementation(libs.lifecycle.runtime.ktx)

    // Testing
}

androidHostTestDependencies {
    implementation(projects.components.core.test)
    implementation(libs.junit)
    implementation(libs.mockito.kotlin)
    implementation(libs.ktx.testing)
    implementation(libs.roboelectric)
    implementation(libs.lifecycle.test)
    implementation(libs.kotlin.coroutines.test)
    implementation(projects.components.updater.downloader)
    implementation(libs.ktor.client)
    implementation(libs.ktor.negotiation)
    implementation(libs.ktor.serialization)
    implementation(libs.ktor.mock)
    implementation(libs.kotlin.serialization.json)
}

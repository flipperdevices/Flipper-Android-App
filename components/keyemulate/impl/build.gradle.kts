plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
}


androidDependencies {
    implementation(projects.components.keyemulate.api)
    implementation(projects.components.keyparser.api)
    implementation(projects.components.screenstreaming.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.data)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.dialog)
    implementation(projects.components.core.ui.lifecycle)

    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.connection.orchestrator.api)
    implementation(projects.components.bridge.synchronization.api)
    implementation(projects.components.bridge.connection.feature.common.api)
    implementation(projects.components.bridge.connection.feature.provider.api)
    implementation(projects.components.bridge.connection.feature.emulate.api)
    implementation(projects.components.bridge.connection.feature.protocolversion.api)
    implementation(projects.components.bridge.connection.pbutils)

    implementation(projects.components.rootscreen.api)

    // Compose
    implementation(libs.compose.placeholder)
    implementation(libs.bundles.decompose)

    implementation(libs.image.lottie)

    implementation(libs.appcompat)

    implementation(libs.lifecycle.compose)

    implementation(libs.lifecycle.runtime.ktx)

    // Testing
}

androidHostTestDependencies {
    implementation(projects.components.core.test)
    implementation(libs.junit)
    implementation(libs.kotlin.coroutines.test)
    implementation(libs.roboelectric)
    implementation(libs.ktx.testing)
    implementation(libs.mockk)
}

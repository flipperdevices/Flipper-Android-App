plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.connection.impl"

androidDependencies {
    implementation(projects.components.connection.api)
    implementation(projects.components.bottombar.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.activityholder)
    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.dialog)
    implementation(projects.components.core.ui.theme)

    implementation(projects.components.bridge.connection.orchestrator.api)
    implementation(projects.components.bridge.synchronization.api)
    implementation(projects.components.bridge.connection.feature.common.api)
    implementation(projects.components.bridge.connection.feature.provider.api)
    implementation(projects.components.bridge.connection.feature.protocolversion.api)

    // Compose
    implementation(libs.image.lottie)
    implementation(libs.decompose)

    implementation(libs.lifecycle.runtime.ktx)

    implementation(libs.lifecycle.compose)
}

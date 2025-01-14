plugins {
    id("flipper.android-lib")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.wearable.emulate.handheld.impl"

dependencies {
    implementation(projects.components.wearable.emulate.common)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.ui.res)

    implementation(projects.components.keyscreen.api)
    implementation(projects.components.keyemulate.api)
    implementation(projects.components.keyparser.api)
    implementation(projects.components.bridge.dao.api)
    // Only for PermissionHelper
    implementation(projects.components.bridge.api)

    implementation(projects.components.bridge.connection.pbutils)
    implementation(projects.components.bridge.connection.feature.common.api)
    implementation(projects.components.bridge.connection.feature.provider.api)
    implementation(projects.components.bridge.connection.feature.protocolversion.api)
    implementation(projects.components.bridge.connection.feature.emulate.api)
    implementation(projects.components.bridge.connection.orchestrator.api)

    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.coroutines.play.services)

    // Dagger deps
    implementation(libs.dagger)

    implementation(libs.wear)
    implementation(libs.wear.gms)

    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.service)
}

plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("kotlinx-serialization")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.remotecontrols.api.backend.flipper"

androidDependencies {
    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.serialization.json)
    implementation(projects.components.core.di)
    implementation(projects.components.remoteControls.coreModel)
    implementation(projects.components.remoteControls.apiBackend)
    implementation(projects.components.faphub.target.api)
    implementation(projects.components.faphub.errors.api)
    implementation(projects.components.faphub.installation.manifest.api)

    implementation(projects.components.bridge.connection.feature.common.api)
    implementation(projects.components.bridge.connection.feature.provider.api)
    implementation(projects.components.bridge.connection.feature.storageinfo.api)
    implementation(projects.components.bridge.connection.feature.rpc.api)
    implementation(projects.components.bridge.connection.orchestrator.api)
    // Only for SDCardException

    implementation(libs.dagger)
    implementation(libs.zacsweers.anvil.annotations)
    implementation(libs.ktor.client)
    implementation(libs.ktor.serialization)
    implementation(libs.ktor.logging)
    implementation(libs.ktor.negotiation)
}

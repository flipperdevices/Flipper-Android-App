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
    implementation(projects.components.bridge.service.api)
    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.rpcinfo.api)
    implementation(projects.components.bridge.rpc.api)

    implementation(libs.dagger)
    implementation(libs.square.anvil.annotations)
    implementation(libs.ktor.client)
    implementation(libs.ktor.serialization)
    implementation(libs.ktor.logging)
    implementation(libs.ktor.negotiation)
}

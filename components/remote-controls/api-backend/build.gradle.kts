plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("kotlinx-serialization")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.remotecontrols.api.backend"

commonDependencies {
    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.serialization.json)
    implementation(projects.components.core.di)
    implementation(projects.components.remoteControls.coreModel)

    implementation(libs.dagger)
    implementation(libs.zacsweers.anvil.annotations)
    implementation(libs.ktor.client)
    implementation(libs.ktor.serialization)
    implementation(libs.ktor.logging)
    implementation(libs.ktor.negotiation)
}

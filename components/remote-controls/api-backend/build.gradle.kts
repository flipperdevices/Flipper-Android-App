plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("kotlinx-serialization")
    id("flipper.anvil")
    id("flipper.ktorfit")
}

android.namespace = "com.flipperdevices.remotecontrols.api.backend"

commonDependencies {
    implementation(libs.ktorfit.lib)
    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.serialization.json)
    implementation(projects.components.core.di)
    implementation(projects.components.remoteControls.coreModel)

    implementation(libs.dagger)
    implementation(libs.square.anvil.annotations)
    implementation(libs.ktor.client)
    implementation(libs.ktor.serialization)
    implementation(libs.ktor.logging)
    implementation(libs.ktor.negotiation)
}

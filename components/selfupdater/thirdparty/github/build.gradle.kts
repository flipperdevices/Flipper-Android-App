plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
    id("kotlinx-serialization")
}

androidDependencies {
    implementation(projects.components.core.buildKonfig)
    implementation(projects.components.selfupdater.thirdparty.api)
    implementation(projects.components.inappnotification.api)

    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.data)

    implementation(libs.lifecycle.runtime.ktx)

    // Ktor deps
    implementation(libs.kotlin.serialization.json)
    implementation(libs.ktor.client)
    implementation(libs.ktor.serialization)
    implementation(libs.ktor.logging)
    implementation(libs.ktor.negotiation)

    // Dagger deps
    implementation(projects.components.core.di)
}

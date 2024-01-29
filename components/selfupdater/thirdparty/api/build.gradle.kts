plugins {
    id("flipper.android-lib")
    id("flipper.anvil")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.selfupdater.thirdparty.api"

dependencies {
    implementation(projects.components.selfupdater.api)
    implementation(projects.components.inappnotification.api)

    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.data)
    implementation(projects.components.core.activityholder)

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

plugins {
    id("flipper.android-lib")
    id("flipper.anvil")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.updater.downloader"

dependencies {
    implementation(projects.components.updater.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.storage)

    implementation(libs.kotlin.serialization.json)

    implementation(libs.ktor.client)
    implementation(libs.ktor.serialization)
    implementation(libs.ktor.logging)
    implementation(libs.ktor.negotiation)

    implementation(libs.apache.compress)
    implementation(libs.apache.codec)
}

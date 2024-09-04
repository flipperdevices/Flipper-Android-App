plugins {
    id("flipper.android-compose")

    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.updater.api"

dependencies {
    implementation(projects.components.bridge.service.api)
    implementation(projects.components.deeplink.api)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.ui.decompose)

    implementation(libs.kotlin.serialization.json)

    implementation(libs.kotlin.coroutines)
    implementation(libs.compose.ui)
    implementation(libs.decompose)
}

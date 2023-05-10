plugins {
    id("flipper.android-lib")
    id("kotlin-parcelize")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.deeplink.api"

dependencies {
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.core.ktx)

    implementation(libs.kotlin.serialization.json)
    implementation(libs.compose.navigation)

    implementation(libs.annotations)
    implementation(libs.appcompat)
}

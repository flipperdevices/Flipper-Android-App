plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.deeplink.api"

commonDependencies {
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.core.ktx)

    implementation(libs.kotlin.serialization.json)
    implementation(libs.compose.ui)

    implementation(libs.annotations)
    implementation(libs.appcompat)
}

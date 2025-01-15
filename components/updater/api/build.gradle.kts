plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.updater.api"

commonDependencies {
    implementation(projects.components.deeplink.api)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.ui.decompose)

    implementation(libs.kotlin.serialization.json)

    implementation(projects.components.bridge.connection.feature.getinfo.api)

    implementation(libs.kotlin.coroutines)
    implementation(libs.compose.ui)
    implementation(libs.decompose)
}

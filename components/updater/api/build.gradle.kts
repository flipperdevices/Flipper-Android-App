plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")

    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.updater.api"

androidDependencies {
    implementation(projects.components.deeplink.api)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.ui.decompose)

    implementation(libs.kotlin.serialization.json)

    implementation(projects.components.bridge.connection.feature.getinfo.api)

    implementation(libs.kotlin.coroutines)
    implementation(libs.decompose)
}

plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")

    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.archive.api"

androidDependencies {
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.synchronization.api)
    implementation(projects.components.core.ui.decompose)

    implementation(projects.components.deeplink.api)

    implementation(libs.decompose)

    implementation(libs.kotlin.serialization.json)
}

plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.wearable.sync.wear.api"

androidDependencies {
    implementation(projects.components.core.ui.decompose)

    // Compose
    implementation(libs.decompose)

    implementation(projects.components.wearable.wearrootscreen.api)
}

plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.notification.api"

androidDependencies {
    implementation(libs.kotlin.coroutines)
    implementation(libs.decompose)

    // Compose
}

plugins {
    id("flipper.android-app-multiplatform")
    id("com.google.devtools.ksp")
    id("flipper.anvil.entrypoint")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.bridge.connection.sample.android"

android {
    defaultConfig {
        applicationId = "com.flipperdevices.bridge.connection"
    }
}

dependencies {
    implementation(projects.components.bridge.connection.sample.shared)
    implementation(projects.components.core.di)
}

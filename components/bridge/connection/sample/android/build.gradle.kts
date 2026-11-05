plugins {
    id("flipper.android-app")
    id("com.google.devtools.ksp")
    id("flipper.anvil")
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
    implementation(projects.components.core.activityholder)
    implementation(projects.components.core.ktx)
}

plugins {
    id("flipper.android-app-multiplatform")
    id("com.google.devtools.ksp")
    id("flipper.anvil.entrypoint")
    id("kotlinx-serialization")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.bridge.connection.sample.android"

android {
    defaultConfig {
        applicationId = "com.flipperdevices.bridge.connection"
    }
}

commonDependencies {
    implementation(projects.components.bridge.connection.sample.shared)
}

androidDependencies {
    implementation(projects.components.core.di)
}

dependencies {
    commonKsp(libs.dagger.compiler)
}

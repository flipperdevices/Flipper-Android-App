plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil-multiplatform")
    alias(libs.plugins.wire)
}

android.namespace = "com.flipperdevices.core.preference"

commonDependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.storage)

    api(libs.datastore)
}

wire {
    kotlin {
        enumMode = "sealed_class"
    }
}

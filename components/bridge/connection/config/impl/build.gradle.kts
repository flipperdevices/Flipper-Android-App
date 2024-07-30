plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil-multiplatform")
}

android.namespace = "com.flipperdevices.bridge.connection.config.impl"

commonDependencies {
    implementation(projects.components.bridge.connection.config.api)

    implementation(projects.components.core.log)
    implementation(projects.components.core.di)
    implementation(projects.components.core.preference)

    implementation(libs.kotlin.coroutines)
    implementation(libs.dagger)
}

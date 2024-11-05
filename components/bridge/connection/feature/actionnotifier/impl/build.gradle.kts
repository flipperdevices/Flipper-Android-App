plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil-multiplatform")
}

android.namespace = "com.flipperdevices.bridge.connection.feature.actionnotifier.impl"

commonDependencies {
    implementation(projects.components.bridge.connection.feature.actionnotifier.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.ktx)

    implementation(libs.kotlin.coroutines)
}

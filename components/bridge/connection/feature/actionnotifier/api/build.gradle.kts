plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.bridge.connection.feature.actionnotifier.api"

commonDependencies {
    implementation(libs.kotlin.coroutines)
}

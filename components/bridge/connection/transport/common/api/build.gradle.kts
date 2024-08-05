plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.bridge.connection.transport.common.api"

commonDependencies {
    implementation(libs.kotlin.coroutines)
}

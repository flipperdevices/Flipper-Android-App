plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.bridge.rpc.api"

dependencies {
    implementation(projects.components.core.progress)

    implementation(projects.components.bridge.api)
    implementation(libs.kotlin.coroutines)
}

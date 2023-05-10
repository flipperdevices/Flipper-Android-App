plugins {
    id("flipper.android-lib")
    id("flipper.protobuf")
}

android.namespace = "com.flipperdevices.bridge.pbutils"

dependencies {
    implementation(projects.components.core.log)
    implementation(libs.kotlin.coroutines)
}

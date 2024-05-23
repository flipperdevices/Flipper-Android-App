plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.bridge.connection.feature.rpc.model"

dependencies {
    implementation(projects.components.core.ktx)
    implementation(projects.components.bridge.pbutils)
}

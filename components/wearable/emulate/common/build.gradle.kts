plugins {
    id("flipper.android-lib")
    id("flipper.protobuf")
}

android.namespace = "com.flipperdevices.wearable.emulate.common"

dependencies {
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)

    implementation(projects.components.bridge.connection.pbutils)

    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.coroutines.play.services)
    implementation(libs.wear.gms)
}

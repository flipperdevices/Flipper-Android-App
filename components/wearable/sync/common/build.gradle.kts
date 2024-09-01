plugins {
    id("flipper.android-compose")

    id("flipper.protobuf")
}

android.namespace = "com.flipperdevices.wearable.sync.common"

dependencies {
    implementation(projects.components.core.ktx)

    implementation(libs.wear.gms)

    implementation(libs.compose.ui)
}

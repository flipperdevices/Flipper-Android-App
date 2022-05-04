plugins {
    androidLibrary
    id("kotlin-android")
    id("kotlin-parcelize")
}

dependencies {
    implementation(projects.components.bridge.pbutils)
    implementation(libs.protobuf.jvm)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)

    implementation(libs.kotlin.coroutines)
    implementation(libs.ble.common)
    implementation(libs.ble.scan)

    implementation(libs.appcompat)
}

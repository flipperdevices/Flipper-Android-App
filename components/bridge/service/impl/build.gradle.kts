plugins {
    id("com.android.library")
    id("kotlin-android")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}
apply<com.flipperdevices.gradle.ConfigurationPlugin>()

dependencies {
    implementation(project(":components:core"))
    implementation(project(":components:bridge:provider"))
    implementation(project(":components:bridge:protobuf"))
    implementation(project(":components:bridge:service:api"))

    implementation(Libs.ANNOTATIONS)
    implementation(Libs.KOTLIN_COROUTINES)
    implementation(Libs.LIFECYCLE_RUNTIME_KTX)
    implementation(Libs.LIFECYCLE_VIEWMODEL_KTX)

    implementation(Libs.NORDIC_BLE)
    implementation(Libs.NORDIC_BLE_KTX)
    implementation(Libs.NORDIC_BLE_COMMON)
    implementation(Libs.NORDIC_BLE_SCAN)

    implementation(Libs.DAGGER)
    kapt(Libs.DAGGER_COMPILER)
}

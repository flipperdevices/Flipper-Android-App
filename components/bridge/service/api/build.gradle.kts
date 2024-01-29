plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.bridge.service.api"

dependencies {
    implementation(projects.components.bridge.api)

    implementation(libs.annotations)
    implementation(libs.appcompat)

    implementation(libs.essenty.lifecycle)
}

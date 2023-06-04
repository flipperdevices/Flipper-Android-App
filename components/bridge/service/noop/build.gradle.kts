plugins {
    id("flipper.android-lib")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.bridge.service.noop"

dependencies {
    implementation(projects.components.bridge.service.api)
    implementation(projects.components.core.di)

    implementation(projects.components.bridge.api)

    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.runtime.ktx)
}

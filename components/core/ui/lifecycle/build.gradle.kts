plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.core.ui.lifecycle"

dependencies {
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.log)
    implementation(projects.components.bridge.service.api)

    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.runtime.ktx)
}

plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.infrared.api"

dependencies {
    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.service.api)

    implementation(libs.kotlin.coroutines)

    implementation(libs.decompose)
}

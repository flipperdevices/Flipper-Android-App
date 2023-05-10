plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.shake2report.api"

dependencies {
    implementation(projects.components.bridge.api)
    implementation(projects.components.core.ui.navigation)

    implementation(libs.kotlin.coroutines)
}

plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.core.test"

dependencies {
    implementation(libs.junit)
    implementation(libs.timber)
    implementation(libs.roboelectric)
    implementation(libs.kotlin.coroutines)
    implementation(libs.mockk)

    implementation(projects.components.core.ui.lifecycle)
}

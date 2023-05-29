plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.faphub.utils"

dependencies {
    implementation(projects.components.core.data)

    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.pbutils)

    implementation(libs.kotlin.coroutines)
}
plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.core.ui.lifecycle"

dependencies {
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.log)
    implementation(projects.components.bridge.service.api)

    api(libs.decompose)
    implementation(libs.kotlin.coroutines)
    api(libs.essenty.lifecycle)
    implementation(libs.essenty.lifecycle.coroutines)

    implementation(libs.annotations)
}

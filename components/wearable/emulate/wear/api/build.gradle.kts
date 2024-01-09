plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.wearable.emulate.api"

dependencies {
    implementation(projects.components.core.ui.decompose)

    implementation(projects.components.bridge.dao.api)

    implementation(libs.kotlin.coroutines)
    implementation(libs.wear.gms)

    implementation(libs.decompose)
}

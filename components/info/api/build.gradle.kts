plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.info.api"

dependencies {
    implementation(projects.components.deeplink.api)

    implementation(projects.components.core.ui.navigation)
    implementation(projects.components.core.ui.decompose)

    implementation(libs.decompose)
}

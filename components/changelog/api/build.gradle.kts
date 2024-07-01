plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.changelog.api"

dependencies {
    implementation(projects.components.core.ui.decompose)

    implementation(projects.components.updater.api)

    implementation(libs.bundles.decompose)
}

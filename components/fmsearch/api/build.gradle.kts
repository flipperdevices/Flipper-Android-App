plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.fmsearch.api"

dependencies {
    implementation(projects.components.core.ui.decompose)
    implementation(libs.bundles.decompose)
}

plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.firstpair.api"

dependencies {
    implementation(projects.components.core.ui.decompose)

    implementation(libs.decompose)
}

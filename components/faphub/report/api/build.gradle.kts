plugins {
    id("flipper.android-lib")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.faphub.report.api"

dependencies {
    implementation(projects.components.core.ui.decompose)

    implementation(libs.decompose)

    implementation(libs.kotlin.serialization.json)
}

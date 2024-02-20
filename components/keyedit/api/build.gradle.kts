plugins {
    id("flipper.android-lib")
    id("kotlin-parcelize")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.keyedit.api"

dependencies {
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.ui.decompose)

    implementation(libs.kotlin.serialization.json)
    implementation(libs.decompose)
}

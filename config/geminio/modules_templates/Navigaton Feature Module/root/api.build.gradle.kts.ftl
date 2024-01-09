plugins {
    id("flipper.android-lib")
}

android.namespace = "${packageName}.api"

dependencies {
    implementation(projects.components.core.ui.decompose)
    implementation(libs.decompose)
}

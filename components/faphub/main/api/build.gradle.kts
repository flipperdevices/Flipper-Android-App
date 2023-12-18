plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.faphub.main.api"

dependencies {
    implementation(projects.components.core.ui.navigation)

    implementation(projects.components.core.ui.decompose)

    implementation(libs.compose.ui)
    implementation(libs.decompose)

}

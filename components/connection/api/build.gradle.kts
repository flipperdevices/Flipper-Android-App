plugins {
    id("flipper.android-compose")
}

android.namespace = "com.flipperdevices.connection.api"

dependencies {
    implementation(projects.components.bottombar.api)
    implementation(libs.compose.ui)

    implementation(libs.decompose)
}

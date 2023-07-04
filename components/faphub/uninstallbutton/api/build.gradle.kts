plugins {
    id("flipper.android-compose")
}

android.namespace = "com.flipperdevices.faphub.uninstallbutton.api"

dependencies {
    implementation(libs.compose.ui)

    implementation(projects.components.faphub.dao.api)
}

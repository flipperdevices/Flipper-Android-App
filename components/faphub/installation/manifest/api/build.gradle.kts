plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.faphub.installation.manifest.api"

dependencies {
    implementation(libs.kotlin.coroutines)
    implementation(projects.components.faphub.dao.api)
}
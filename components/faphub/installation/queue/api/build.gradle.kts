plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.faphub.installation.queue.api"

dependencies {
    implementation(projects.components.faphub.dao.api)
    implementation(projects.components.faphub.installation.manifest.api)

    implementation(libs.kotlin.coroutines)
}

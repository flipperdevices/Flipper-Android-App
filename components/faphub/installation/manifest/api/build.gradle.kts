plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.faphub.installation.manifest.api"

dependencies {
    implementation(projects.components.faphub.dao.api)
    implementation(projects.components.core.data)

    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.immutable.collections)
}

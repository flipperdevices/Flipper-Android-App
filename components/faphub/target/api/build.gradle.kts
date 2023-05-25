plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.faphub.target.api"

dependencies {
    implementation(projects.components.core.data)

    implementation(libs.kotlin.coroutines)
}

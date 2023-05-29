plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.faphub.constants"

dependencies {
    implementation(projects.components.core.data)
}
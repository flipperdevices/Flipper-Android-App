plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.core.di"

dependencies {
    implementation(libs.dagger)
}

plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.core.log"

dependencies {
    implementation(libs.timber)
}

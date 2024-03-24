plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.core.progress"

dependencies {

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlin.coroutines.test)
}

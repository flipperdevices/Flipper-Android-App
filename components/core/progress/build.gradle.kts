plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.core.progress"

commonTestDependencies {
    // Testing
    implementation(libs.junit)
    implementation(libs.mockk)
    implementation(libs.kotlin.coroutines.test)
}

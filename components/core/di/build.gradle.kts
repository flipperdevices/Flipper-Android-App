plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("dev.zacsweers.metro")
}

android.namespace = "com.flipperdevices.core.di"

jvmSharedTestDependencies {
    implementation(libs.junit)
    implementation(libs.kotlin.coroutines.test)
}

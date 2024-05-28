plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.core.di"

commonDependencies {
    implementation(libs.dagger)
}

jvmSharedTestDependencies {
    implementation(libs.junit)
    implementation(libs.kotlin.coroutines.test)
}

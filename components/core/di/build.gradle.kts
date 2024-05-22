plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.core.di"

commonMainDependencies {
    implementation(libs.dagger)
}

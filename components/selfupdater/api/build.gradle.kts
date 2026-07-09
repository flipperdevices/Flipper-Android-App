plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.selfupdater.api"

androidDependencies {
    implementation(libs.kotlin.coroutines)
}

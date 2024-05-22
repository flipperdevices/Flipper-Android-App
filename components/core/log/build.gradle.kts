plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.core.log"

androidMainDependencies {
    implementation(libs.timber)
}

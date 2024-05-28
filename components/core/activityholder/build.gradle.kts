plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.core.activityholder"

androidDependencies {
    implementation(libs.appcompat)
}

plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.core.activityholder"

androidMainDependencies {
    implementation(libs.appcompat)
}

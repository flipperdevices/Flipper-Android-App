plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.core.ui.res"

androidDependencies {
    implementation(libs.appcompat)
}

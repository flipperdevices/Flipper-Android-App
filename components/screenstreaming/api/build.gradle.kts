plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.screenstreaming.api"

androidDependencies {
    implementation(projects.components.core.ui.decompose)
    implementation(libs.decompose)
}

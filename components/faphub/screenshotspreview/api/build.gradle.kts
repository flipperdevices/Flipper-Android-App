plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.faphub.screenshotspreview.api"

commonDependencies {
    implementation(projects.components.core.ui.decompose)
    implementation(libs.kotlin.immutable.collections)
    implementation(libs.decompose)
}

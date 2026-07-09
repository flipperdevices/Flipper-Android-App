plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.firstpair.api"

androidDependencies {
    implementation(projects.components.core.ui.decompose)

    implementation(libs.decompose)
}

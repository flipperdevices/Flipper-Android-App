plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.remotecontrols.categories.api"

androidDependencies {
    implementation(projects.components.core.ui.decompose)

    implementation(libs.decompose)
}

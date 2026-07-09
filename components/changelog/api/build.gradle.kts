plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.changelog.api"

androidDependencies {
    implementation(projects.components.core.ui.decompose)

    implementation(projects.components.updater.api)

    implementation(libs.bundles.decompose)
}

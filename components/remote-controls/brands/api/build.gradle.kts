plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.remotecontrols.brands.api"

androidDependencies {
    implementation(projects.components.core.ui.decompose)

    implementation(libs.decompose)
}

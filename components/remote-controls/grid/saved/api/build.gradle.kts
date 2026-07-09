plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.remotecontrols.grid.saved.api"

androidDependencies {
    implementation(projects.components.bridge.dao.api)

    implementation(projects.components.core.ui.decompose)

    implementation(libs.decompose)
}

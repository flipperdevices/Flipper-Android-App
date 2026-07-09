plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
    id("kotlinx-serialization")
}


androidDependencies {
    implementation(projects.components.faphub.screenshotspreview.api)

    implementation(projects.components.core.ui.decompose)

    implementation(projects.components.updater.api)
    implementation(projects.components.deeplink.api)
    implementation(projects.components.bridge.dao.api)

    implementation(libs.decompose)
}

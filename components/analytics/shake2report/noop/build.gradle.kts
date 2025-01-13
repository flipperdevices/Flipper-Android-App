plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil-multiplatform")
}

android.namespace = "com.flipperdevices.shake2report.noop"

commonDependencies {
    implementation(projects.components.analytics.shake2report.api)
    implementation(projects.components.core.di)
    implementation(projects.components.core.ui.decompose)

    implementation(libs.annotations)

    implementation(libs.bundles.decompose)
}

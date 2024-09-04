plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.shake2report.api"

commonDependencies {
    implementation(projects.components.core.ui.decompose)

    implementation(libs.kotlin.coroutines)

    implementation(libs.decompose)
}

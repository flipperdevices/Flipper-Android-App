plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.infrared.api"

androidDependencies {
    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.bridge.dao.api)

    implementation(libs.kotlin.coroutines)

    implementation(libs.decompose)
}

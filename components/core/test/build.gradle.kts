plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.core.test"

commonDependencies {
    implementation(libs.kotlin.coroutines)
    implementation(libs.mockk)

    implementation(projects.components.core.ui.lifecycle)
}

androidDependencies {
    implementation(libs.junit)
    implementation(libs.timber)
    implementation(libs.roboelectric)
}

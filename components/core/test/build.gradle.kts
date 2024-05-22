plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.core.test"

commonMainDependencies {
    implementation(libs.kotlin.coroutines)
    implementation(libs.mockk)

    implementation(projects.components.core.ui.lifecycle)
}

androidMainDependencies {
    implementation(libs.junit)
    implementation(libs.timber)
    implementation(libs.roboelectric)
}

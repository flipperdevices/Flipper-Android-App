plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.core.test"

commonDependencies {
    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.core.storage)

    implementation(libs.kotlin.coroutines)
    implementation(libs.mockk)
    implementation(libs.okio)
    implementation(libs.okio.fake)
    implementation(libs.junit)
}

androidDependencies {
    implementation(libs.junit)
    implementation(libs.timber)
    implementation(libs.roboelectric)
}

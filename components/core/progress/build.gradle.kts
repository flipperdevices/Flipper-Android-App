plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.core.progress"

commonDependencies {
    implementation(projects.components.core.buildKonfig)
    implementation(libs.okio)
}

commonTestDependencies {
    implementation(libs.junit)
    implementation(libs.mockk)
    implementation(libs.kotlin.coroutines.test)
}

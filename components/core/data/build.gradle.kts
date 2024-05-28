plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.core.data"

commonDependencies {
    implementation(libs.kotlin.immutable.collections)
    implementation(libs.compose.ui)
}

commonTestDependencies {
    implementation(libs.junit)
    implementation(libs.mockito.kotlin)
    implementation(libs.ktx.testing)
}

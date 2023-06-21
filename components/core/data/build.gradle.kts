plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.core.data"

dependencies {
    implementation(libs.kotlin.immutable.collections)
    implementation(libs.compose.ui)

    testImplementation(projects.components.core.test)
    testImplementation(libs.junit)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.ktx.testing)
}

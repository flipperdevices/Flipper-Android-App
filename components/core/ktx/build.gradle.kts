plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.core.ktx"

commonMainDependencies {
    implementation(projects.components.core.log)

    implementation(libs.appcompat)
    implementation(libs.kotlin.coroutines)

    implementation(libs.kotlin.coroutines)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
}

commonTestDependencies {
    implementation(projects.components.core.test)
    implementation(libs.junit)
}

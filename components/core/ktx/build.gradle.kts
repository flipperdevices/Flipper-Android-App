plugins {
    id("flipper.android-compose")
}

dependencies {
    implementation(projects.components.core.log)

    implementation(libs.appcompat)
    implementation(libs.kotlin.coroutines)

    implementation(libs.kotlin.coroutines)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)

    testImplementation(projects.components.core.test)
    testImplementation(libs.junit)
}

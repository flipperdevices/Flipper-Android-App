plugins {
    id("flipper.android-compose")
}

android.namespace = "com.flipperdevices.core.markdown"

dependencies {
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.theme)

    implementation(libs.annotations)
    implementation(libs.appcompat)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)

    implementation(libs.flexmark.core)
    implementation(libs.markdown.renderer) {
        exclude(libs.fastutil.get().group)
    }

    // Testing
    testImplementation(projects.components.core.test)
    testImplementation(libs.junit)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.ktx.testing)
    testImplementation(libs.roboelectric)
    testImplementation(libs.lifecycle.test)
    testImplementation(libs.kotlin.coroutines.test)
}

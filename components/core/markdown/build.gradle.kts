plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.core.markdown"

androidDependencies {
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.theme)

    implementation(libs.annotations)
    implementation(libs.appcompat)

    // Compose

    implementation(libs.flexmark.core)
    api(libs.markdown.renderer)
}

configurations.named("androidMainApi") {
    exclude(group = libs.fastutil.get().group)
}

dependencies {
    "androidUnitTestImplementation"(projects.components.core.test)
    "androidUnitTestImplementation"(libs.junit)
    "androidUnitTestImplementation"(libs.mockito.kotlin)
    "androidUnitTestImplementation"(libs.ktx.testing)
    "androidUnitTestImplementation"(libs.roboelectric)
    "androidUnitTestImplementation"(libs.lifecycle.test)
    "androidUnitTestImplementation"(libs.kotlin.coroutines.test)
}

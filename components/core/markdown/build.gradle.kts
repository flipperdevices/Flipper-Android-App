plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}

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

androidHostTestDependencies {
    implementation(projects.components.core.test)
    implementation(libs.junit)
    implementation(libs.mockito.kotlin)
    implementation(libs.ktx.testing)
    implementation(libs.roboelectric)
    implementation(libs.lifecycle.test)
    implementation(libs.kotlin.coroutines.test)
}

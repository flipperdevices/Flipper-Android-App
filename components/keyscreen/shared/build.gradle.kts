plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}


androidDependencies {
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.ktx)

    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.keyparser.api)

    implementation(libs.kotlin.immutable.collections)

    // Compose
    implementation(libs.compose.constraint)
}

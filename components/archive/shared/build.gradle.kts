plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.archive.shared"

androidDependencies {
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.keyparser.api)

    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.theme)

    // Compose

    // Testing
}

dependencies {
    "androidUnitTestImplementation"(libs.junit)
    "androidUnitTestImplementation"(libs.kotlin.immutable.collections)
}

plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil-multiplatform")
}

android.namespace = "com.flipperdevices.core.ui.theme"

commonDependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.preference)

    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.compose)
}

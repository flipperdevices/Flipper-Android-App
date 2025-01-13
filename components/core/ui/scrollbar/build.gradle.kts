plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.core.ui.scrollbar"

commonDependencies {
    implementation(projects.components.core.ui.theme)
}

plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.tabswitch"

commonDependencies {
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.ktx)
}

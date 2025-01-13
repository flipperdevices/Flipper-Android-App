plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.core.ui.flippermockup"

commonDependencies {
    implementation(projects.components.core.ui.theme)

    implementation(projects.components.core.preference)
}

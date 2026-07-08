plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.singleactivity.api"

androidDependencies {
    implementation(projects.components.deeplink.api)
}

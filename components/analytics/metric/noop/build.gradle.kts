plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil-multiplatform")
}

android.namespace = "com.flipperdevices.metric.noop"

commonDependencies {
    implementation(projects.components.analytics.metric.api)

    implementation(projects.components.core.di)
}

plugins {
    id("flipper.android-lib")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.metric.noop"

dependencies {
    implementation(projects.components.analytics.metric.api)

    implementation(projects.components.core.di)
}

plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.wearable.sync.handheld.noop"

androidDependencies {
    implementation(projects.components.wearable.sync.handheld.api)

    implementation(projects.components.core.di)
}

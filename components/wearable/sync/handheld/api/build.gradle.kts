plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.wearable.sync.handheld.api"

commonDependencies {
    implementation(projects.components.bridge.dao.api)
}

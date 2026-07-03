plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.infrared.utils"

androidDependencies {
    implementation(projects.components.bridge.dao.api)

    implementation(projects.components.core.ktx)
}

plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.filemanager.util"

commonDependencies {
    implementation(projects.components.bridge.dao.api)

    implementation(libs.kotlin.serialization.json)

    implementation(libs.okio)
}

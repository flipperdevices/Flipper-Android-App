plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.faphub.utils"

androidDependencies {
    implementation(projects.components.core.data)

    implementation(libs.kotlin.coroutines)
}

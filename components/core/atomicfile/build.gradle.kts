plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.core.atomicfile"

commonDependencies {
    implementation(projects.components.core.log)

    implementation(libs.okio)
    implementation(libs.kotlin.coroutines)
}

androidDependencies {
    implementation(libs.androidx.core)
}

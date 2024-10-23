plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil-multiplatform")
}

android.namespace = "com.flipperdevices.core.storage"

commonDependencies {
    implementation(projects.components.core.log)
    implementation(projects.components.core.di)

    implementation(libs.okio)
    implementation(libs.kotlin.coroutines)
}

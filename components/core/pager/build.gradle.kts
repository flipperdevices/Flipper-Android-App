plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.core.pager"

androidDependencies {
    implementation(projects.components.core.log)

    implementation(libs.compose.paging)
}

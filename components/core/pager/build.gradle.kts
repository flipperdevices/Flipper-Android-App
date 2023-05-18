plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.core.pager"

dependencies {
    implementation(projects.components.core.log)

    implementation(libs.compose.paging)
}

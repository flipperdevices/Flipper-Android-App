plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.core.pager"

dependencies {
    implementation(libs.compose.paging)
}

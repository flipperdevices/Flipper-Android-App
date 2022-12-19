plugins {
    id("flipper.lint")
    id("flipper.android-lib")
    id("flipper.protobuf")
}

dependencies {
    implementation(projects.components.core.log)
    implementation(libs.kotlin.coroutines)
    api(libs.protobuf.kotlinlite)
}

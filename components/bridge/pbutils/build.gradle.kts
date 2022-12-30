plugins {
    id("flipper.android-lib")
    id("flipper.protobuf")
}

dependencies {
    implementation(projects.components.core.log)
    implementation(libs.kotlin.coroutines)
}

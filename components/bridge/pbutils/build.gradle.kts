plugins {
    id("flipper.android-lib")
}

dependencies {
    implementation(libs.protobuf.jvm)

    implementation(projects.components.core.log)
    implementation(libs.kotlin.coroutines)
}

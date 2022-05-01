plugins {
    androidLibrary
}

dependencies {
    // Protobuf jvm
    implementation(libs.protobuf.jvm)

    implementation(projects.components.core.log)
    implementation(libs.kotlin.coroutines)
}
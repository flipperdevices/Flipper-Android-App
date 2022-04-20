plugins {
    androidLibrary
}

dependencies {
    implementation(projects.components.core.log)

    implementation(libs.appcompat)
    implementation(libs.kotlin.coroutines)
}

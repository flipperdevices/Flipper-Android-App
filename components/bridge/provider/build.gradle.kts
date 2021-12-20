plugins {
    androidLibrary
}

dependencies {
    api(projects.components.bridge.api)
    implementation(projects.components.bridge.impl)
}

plugins {
    id("androidCompose")
}

dependencies {
    implementation(projects.components.bridge.dao.api)

    implementation(libs.compose.ui)
}

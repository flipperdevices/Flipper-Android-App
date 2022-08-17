plugins {
    id("flipper.lint")
    id("androidCompose")
}

dependencies {
    implementation(projects.components.bridge.dao.api)

    implementation(libs.compose.ui)
}

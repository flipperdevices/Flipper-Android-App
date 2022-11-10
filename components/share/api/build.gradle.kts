plugins {
    id("flipper.lint")
    id("flipper.android-compose")
}

dependencies {
    implementation(projects.components.deeplink.api)
    implementation(libs.compose.ui)
    implementation(project(mapOf("path" to ":components:bridge:dao:api")))
}

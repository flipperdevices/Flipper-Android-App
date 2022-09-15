plugins {
    id("flipper.lint")
    id("flipper.android-lib")
}

dependencies {
    implementation(projects.components.bridge.dao.api)
}

plugins {
    id("flipper.lint")
    id("flipper.android-lib")
}

dependencies {
    implementation(projects.components.faphub.dao.api)
    implementation(projects.components.core.ui.navigation)
}
plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

androidDependencies {
    implementation(libs.metro.utils.annotations)
    implementation(projects.components.faphub.dao.api)
    implementation(projects.components.core.ui.decompose)
    implementation(libs.decompose)
}

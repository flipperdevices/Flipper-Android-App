plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

androidDependencies {
    implementation(projects.components.core.ui.decompose)

    implementation(projects.components.bridge.dao.api)

    implementation(libs.kotlin.coroutines)
    implementation(libs.wear.gms)

    implementation(libs.decompose)
}

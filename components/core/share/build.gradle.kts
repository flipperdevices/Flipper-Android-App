plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
}

commonDependencies {
    implementation(projects.components.core.buildKonfig)
    implementation(projects.components.core.di)
    implementation(libs.okio)
}

androidDependencies {
    implementation(projects.components.core.ktx)
    implementation(libs.annotations)
    implementation(libs.appcompat)
}

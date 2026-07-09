plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
}

androidDependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.permission.api)
    implementation(libs.appcompat)

    implementation(libs.ktx.activity)
}

plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}


commonDependencies {
    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.deeplink.api)

    implementation(projects.components.filemngr.listing.api)

    implementation(libs.decompose)

    implementation(libs.okio)
}

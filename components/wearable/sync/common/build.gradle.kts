plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
    id("flipper.wire")
}

androidDependencies {
    implementation(projects.components.core.ktx)

    implementation(libs.wear.gms)
}

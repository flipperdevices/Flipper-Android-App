plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}

androidDependencies {

    implementation(projects.components.faphub.dao.api)
}

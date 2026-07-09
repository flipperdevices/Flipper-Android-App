plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
    id("kotlinx-serialization")
}


commonDependencies {
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.kmpparcelize)

    implementation(libs.kotlin.serialization.json)

    implementation(libs.annotations)
}

androidDependencies {
    implementation(libs.appcompat)
}

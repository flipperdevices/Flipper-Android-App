plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}


androidDependencies {
    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.infrared.utils)
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.remoteControls.coreModel)
    implementation(projects.components.keyemulate.api)

    implementation(projects.components.bridge.connection.feature.common.api)
    implementation(projects.components.bridge.connection.feature.provider.api)
    implementation(projects.components.bridge.connection.feature.emulate.api)

    implementation(libs.decompose)
}

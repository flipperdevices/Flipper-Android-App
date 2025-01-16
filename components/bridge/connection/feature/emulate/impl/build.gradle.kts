plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil-multiplatform")
}

android.namespace = "com.flipperdevices.bridge.connection.feature.emulate.impl"

commonDependencies {
    implementation(projects.components.bridge.connection.feature.emulate.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.data)
    implementation(projects.components.core.kmpparcelize)

    implementation(projects.components.bridge.connection.feature.common.api)
    implementation(projects.components.bridge.connection.transport.common.api)
    implementation(projects.components.bridge.connection.feature.rpc.api)
    implementation(projects.components.bridge.connection.feature.rpc.model)
    implementation(projects.components.bridge.connection.feature.rpcinfo.api)
    implementation(projects.components.bridge.connection.feature.protocolversion.api)
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.connection.pbutils)

    implementation(projects.components.bridge.connection.pbutils)

    implementation(libs.kotlin.coroutines)
}

commonTestDependencies {
    // Testing
    implementation(projects.components.core.test)
    implementation(libs.junit)
    implementation(libs.kotlin.coroutines.test)
    implementation(libs.roboelectric)
    implementation(libs.ktx.testing)
    implementation(libs.mockk)
}

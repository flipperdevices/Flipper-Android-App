plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil-multiplatform")
}

android.namespace = "com.flipperdevices.bridge.connection.feature.storage.impl"

commonDependencies {
    implementation(projects.components.bridge.connection.feature.storage.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.data)
    implementation(projects.components.core.progress)

    implementation(projects.components.bridge.connection.transport.common.api)
    implementation(projects.components.bridge.connection.feature.common.api)
    implementation(projects.components.bridge.connection.feature.rpc.api)
    implementation(projects.components.bridge.connection.feature.protocolversion.api)
    implementation(projects.components.bridge.connection.pbutils)

    implementation(libs.kotlin.coroutines)

    implementation(libs.okio)
}

commonTestDependencies {
    implementation(libs.mockk)
    implementation(libs.kotlin.coroutines.test)
    implementation(libs.kotlin.test)
}

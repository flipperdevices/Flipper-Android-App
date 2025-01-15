plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.bridge.connection.feature.emulate.api"

commonDependencies {
    implementation(projects.components.core.data)
    implementation(projects.components.core.ktx)

    implementation(projects.components.bridge.connection.feature.common.api)
    implementation(projects.components.bridge.connection.transport.common.api)
    implementation(projects.components.bridge.connection.feature.rpcinfo.api)
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.connection.pbutils)

    implementation(libs.kotlin.immutable.collections)
    implementation(libs.kotlin.coroutines)
}

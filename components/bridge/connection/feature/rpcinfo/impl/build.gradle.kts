plugins {
    id("flipper.android-lib")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.bridge.connection.feature.rpcinfo.impl"

dependencies {
    implementation(projects.components.bridge.connection.feature.rpcinfo.api)

    implementation(projects.components.core.data)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.di)
    implementation(projects.components.core.log)

    implementation(projects.components.bridge.connection.feature.common.api)
    implementation(projects.components.bridge.connection.transport.common.api)
    implementation(projects.components.bridge.connection.feature.rpc.api)
    implementation(projects.components.bridge.connection.feature.rpc.model)
    implementation(projects.components.bridge.connection.feature.getinfo.api)

    implementation(projects.components.bridge.pbutils)
    implementation(projects.components.analytics.shake2report.api)

    implementation(libs.kotlin.immutable.collections)
    implementation(libs.kotlin.coroutines)
}

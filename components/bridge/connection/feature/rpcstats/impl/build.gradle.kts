plugins {
    id("flipper.android-lib")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.bridge.connection.feature.rpcstats.impl"

dependencies {
    implementation(projects.components.bridge.connection.feature.rpcstats.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)

    implementation(projects.components.analytics.metric.api)
    implementation(projects.components.analytics.shake2report.api)

    implementation(projects.components.bridge.connection.feature.common.api)
    implementation(projects.components.bridge.connection.transport.common.api)
    implementation(projects.components.bridge.connection.feature.storageinfo.api)
    implementation(projects.components.bridge.connection.feature.rpcinfo.api)
    implementation(projects.components.bridge.connection.feature.getinfo.api)

    implementation(projects.components.bridge.pbutils)

    implementation(libs.kotlin.coroutines)
}

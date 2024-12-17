plugins {
    id("flipper.android-lib")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.faphub.installation.manifest.impl"

dependencies {
    implementation(projects.components.faphub.installation.manifest.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.data)
    implementation(projects.components.core.log)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.progress)

    implementation(projects.components.settings.api)

    implementation(projects.components.faphub.dao.api)
    implementation(projects.components.faphub.utils)
    implementation(projects.components.faphub.errors.api)

    implementation(projects.components.bridge.connection.feature.common.api)
    implementation(projects.components.bridge.connection.feature.rpcinfo.api)
    implementation(projects.components.bridge.connection.feature.provider.api)
    implementation(projects.components.bridge.connection.feature.storage.api)
    implementation(projects.components.bridge.connection.feature.protocolversion.api)
    implementation(projects.components.bridge.connection.feature.storageinfo.api)
    implementation(projects.components.bridge.connection.orchestrator.api)

//    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.dao.api)
//    implementation(projects.components.bridge.rpc.api)
//    implementation(projects.components.bridge.rpcinfo.api)
//    implementation(projects.components.bridge.service.api)
//    implementation(projects.components.bridge.pbutils)

    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.immutable.collections)
    implementation(libs.okio)
}

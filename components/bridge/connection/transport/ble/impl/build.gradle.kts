plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil-multiplatform")
}

android.namespace = "com.flipperdevices.bridge.connection.transport.ble.impl"

androidDependencies {
    implementation(projects.components.bridge.connection.transport.ble.api)

    implementation(projects.components.core.log)
    implementation(projects.components.core.di)
    implementation(projects.components.core.ktx)

    implementation(projects.components.bridge.connection.transport.common.api)
    implementation(projects.components.bridge.connection.feature.actionnotifier.api)

    implementation(libs.ble.kotlin.scanner)
    implementation(libs.ble.kotlin.client)

    implementation(libs.annotations)

    implementation(libs.kotlin.coroutines)

    implementation(libs.slf4j.timber)
    implementation(libs.kotlin.immutable.collections)
}

androidUnitTestDependencies {
    implementation(projects.components.core.test)
    implementation(libs.kotlin.coroutines.test)
    implementation(libs.junit)
    implementation(libs.mockk)
}

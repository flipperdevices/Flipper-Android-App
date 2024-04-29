plugins {
    id("flipper.android-lib")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.bridge.connection.transport.ble.impl"

dependencies {
    implementation(projects.components.bridge.connection.transport.ble.api)

    implementation(projects.components.core.log)
    implementation(projects.components.core.di)
    implementation(projects.components.core.ktx)

    implementation(projects.components.bridge.connection.transport.common.api)

    implementation(libs.ble.kotlin.scanner)
    implementation(libs.ble.kotlin.client)

    implementation(libs.annotations)

    implementation(libs.kotlin.coroutines)

    implementation(libs.slf4j.timber)

    testImplementation(projects.components.core.test)
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
}

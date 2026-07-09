plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
}


androidDependencies {
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.di)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.data)
    implementation(projects.components.analytics.shake2report.api)
    implementation(projects.components.analytics.metric.api)

    implementation(projects.components.firstpair.connection.api)

    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.immutable.collections)
    implementation(libs.annotations)
    implementation(libs.ktx)
    implementation(libs.appcompat)

    implementation(libs.ble.scan)
    implementation(libs.ble)
    implementation(libs.ble.common)

    implementation(libs.fastutil)

    // Testing
}

androidHostTestDependencies {
    implementation(projects.components.core.test)
    implementation(projects.components.core.buildKonfig)
    implementation(libs.junit)
    implementation(libs.mockk)
    implementation(libs.mockito.kotlin)
    implementation(libs.ktx.testing)
    implementation(libs.roboelectric)
    implementation(libs.lifecycle.test)
    implementation(libs.kotlin.coroutines.test)
}

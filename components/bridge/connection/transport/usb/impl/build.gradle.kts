plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
}

commonDependencies {
    implementation(projects.components.bridge.connection.transport.usb.api)

    implementation(projects.components.core.log)
    implementation(projects.components.core.di)
    implementation(projects.components.core.ktx)

    implementation(projects.components.bridge.connection.transport.common.api)
    implementation(projects.components.bridge.connection.feature.actionnotifier.api)

    implementation(libs.annotations)
    implementation(libs.kotlin.immutable.collections)
    implementation(libs.kotlin.coroutines)
}

desktopDependencies {
    implementation(libs.jserial)
}

androidDependencies {
    implementation(libs.ktx)
    implementation(libs.usb.android)
}

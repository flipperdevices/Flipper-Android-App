plugins {
    id("flipper.android-lib")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.faphub.target.impl"

dependencies {
    implementation(projects.components.faphub.target.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.data)

    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.pbutils)
    implementation(projects.components.bridge.service.api)
    implementation(projects.components.bridge.rpcinfo.api)

    implementation(projects.components.faphub.utils)

    implementation(libs.kotlin.coroutines)
}

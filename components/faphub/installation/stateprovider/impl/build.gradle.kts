plugins {
    id("flipper.android-lib")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.faphub.installation.stateprovider.impl"

dependencies {
    implementation(projects.components.faphub.installation.stateprovider.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.data)

    implementation(projects.components.bridge.api)

    implementation(projects.components.faphub.dao.api)
    implementation(projects.components.faphub.installation.manifest.api)
    implementation(projects.components.faphub.installation.queue.api)
    implementation(projects.components.faphub.target.api)

    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.immutable.collections)
}

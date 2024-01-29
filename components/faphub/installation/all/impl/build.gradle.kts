plugins {
    id("flipper.android-lib")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.faphub.installation.all.impl"

dependencies {
    implementation(projects.components.faphub.installation.all.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)

    implementation(projects.components.faphub.installation.queue.api)
    implementation(projects.components.faphub.installation.manifest.api)
    implementation(projects.components.faphub.target.api)
    implementation(projects.components.faphub.dao.api)

    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.immutable.collections)
}

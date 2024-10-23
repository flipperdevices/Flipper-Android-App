plugins {
    id("flipper.android-lib")
    id("flipper.anvil")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.faphub.dao.network"

dependencies {
    implementation(projects.components.faphub.dao.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.data)
    implementation(projects.components.core.storage)
    implementation(projects.components.core.progress)
    implementation(projects.components.core.preference)

    implementation(projects.components.faphub.target.api)
    implementation(projects.components.faphub.errors.api)

    implementation(libs.kotlin.serialization.json)

    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.immutable.collections)
    implementation(libs.kotlin.datetime)
    implementation(libs.ktor.client)

    implementation(libs.annotations)
}

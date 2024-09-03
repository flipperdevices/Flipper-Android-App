plugins {
    id("flipper.android-lib")

    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.faphub.dao.api"

dependencies {
    implementation(projects.components.core.data)
    implementation(projects.components.core.progress)
    implementation(projects.components.core.preference)

    implementation(libs.kotlin.serialization.json)
    implementation(libs.kotlin.immutable.collections)

    implementation(projects.components.faphub.target.api)

    implementation(libs.annotations)
    implementation(libs.compose.ui)
}

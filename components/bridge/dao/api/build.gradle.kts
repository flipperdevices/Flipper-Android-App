plugins {
    id("flipper.android-compose")
    id("kotlinx-serialization")
    id("kotlin-parcelize")
}

android.namespace = "com.flipperdevices.bridge.dao.api"

dependencies {
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ktx)

    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.serialization.json)
    implementation(libs.kotlin.immutable.collections)

    implementation(libs.compose.ui)
}

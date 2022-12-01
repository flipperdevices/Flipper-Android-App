plugins {
    id("flipper.lint")
    id("flipper.android-lib")
    id("kotlin-parcelize")
    id("kotlinx-serialization")
}

dependencies {
    implementation(projects.components.core.data)

    implementation(libs.kotlin.serialization.json)
}

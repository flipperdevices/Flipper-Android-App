plugins {
    id("flipper.lint")
    id("flipper.android-lib")
    id("kotlin-parcelize")
    id("kotlinx-serialization")
}

dependencies {
    implementation(libs.kotlin.serialization.json)
}

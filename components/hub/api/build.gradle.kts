plugins {
    id("flipper.lint")
    id("flipper.android-lib")
}

dependencies {
    implementation(libs.cicerone)
    implementation(libs.kotlin.coroutines)
}

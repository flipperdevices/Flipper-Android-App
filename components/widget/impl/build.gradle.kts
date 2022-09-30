plugins {
    id("flipper.lint")
    id("flipper.android-lib")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

dependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.ui.res)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)

}
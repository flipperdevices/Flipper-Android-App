plugins {
    androidLibrary
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

dependencies {
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}

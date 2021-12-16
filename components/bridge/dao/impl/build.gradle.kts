plugins {
    androidLibrary
    id("com.squareup.anvil")
    id("kotlin-kapt")
    id("com.google.devtools.ksp")
}

dependencies {
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.ksp)

    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}

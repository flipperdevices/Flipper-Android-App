plugins {
    id("flipper.android-lib")
    id("com.squareup.anvil")
    id("flipper.protobuf")
    id("kotlin-kapt")
}

dependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.share)

    api(libs.datastore)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}

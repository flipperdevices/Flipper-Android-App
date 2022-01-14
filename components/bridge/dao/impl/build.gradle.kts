plugins {
    androidLibrary
    id("com.squareup.anvil")
    id("kotlin-kapt")
    id("com.google.devtools.ksp")
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    implementation(projects.components.bridge.dao.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.preference)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.ksp)

    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}

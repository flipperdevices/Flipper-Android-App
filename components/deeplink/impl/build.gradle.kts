plugins {
    androidLibrary
    id("com.squareup.anvil")
    kotlin("kapt")
}

dependencies {
    implementation(project(":components:core:di"))
    implementation(project(":components:core:log"))

    implementation(project(":components:bridge:dao"))

    implementation(project(":components:deeplink:api"))
    implementation(project(":components:filemanager:api"))

    implementation(Libs.ANNOTATIONS)
    implementation(Libs.APPCOMPAT)
    implementation(Libs.KOTLIN_COROUTINES)

    // Dagger deps
    implementation(Libs.DAGGER)
    kapt(Libs.DAGGER_COMPILER)
}

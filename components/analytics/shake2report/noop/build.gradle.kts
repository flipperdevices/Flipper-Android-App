plugins {
    javaLibrary
    id("com.squareup.anvil")
    kotlin("kapt")
}

dependencies {
    implementation(project(":components:analytics:shake2report:api"))
    implementation(project(":components:core:di"))

    implementation(Libs.ANNOTATIONS)
    implementation(Libs.APPCOMPAT)

    // Dagger deps
    implementation(Libs.DAGGER)
    kapt(Libs.DAGGER_COMPILER)
}

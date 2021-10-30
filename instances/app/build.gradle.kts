plugins {
    id("com.android.application")
    id("com.squareup.anvil")
    id("kotlin-android")
    id("kotlin-kapt")
}

apply<com.flipperdevices.gradle.ConfigurationPlugin>()

dependencies {
    implementation(project(":components:core"))
    implementation(project(":components:pair:api"))
    implementation(project(":components:pair:impl"))
    implementation(project(":components:info"))
    implementation(project(":components:bottombar"))
    implementation(project(":components:filemanager:api"))
    implementation(project(":components:filemanager:impl"))
    implementation(project(":components:bridge:service:api"))
    implementation(project(":components:bridge:service:impl"))
    debugImplementation(project(":components:analytics:shake2report"))
    add("internalImplementation", project(":components:analytics:shake2report"))

    implementation(Libs.ANNOTATIONS)
    implementation(Libs.CORE_KTX)
    implementation(Libs.APPCOMPAT)
    implementation(Libs.MATERIAL)
    implementation(Libs.TREX)

    implementation(Libs.DAGGER)
    kapt(Libs.DAGGER_COMPILER)
}

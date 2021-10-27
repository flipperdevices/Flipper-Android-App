plugins {
    id("com.android.library")
    id("com.squareup.anvil")
    id("kotlin-android")
    id("kotlin-kapt")
}
apply<com.flipperdevices.gradle.ConfigurationPlugin>()
apply<com.flipperdevices.gradle.ComposerPlugin>()

dependencies {
    implementation(project(":components:core"))
    implementation(project(":components:bridge:service:api"))
    implementation(project(":components:bridge:api"))
    implementation(project(":components:bridge:protobuf"))
    implementation(project(":components:filemanager:api"))

    implementation(Libs.ANNOTATIONS)
    implementation(Libs.APPCOMPAT)

    implementation(Libs.COMPOSE_UI)
    implementation(Libs.COMPOSE_TOOLING)
    implementation(Libs.COMPOSE_FOUNDATION)
    implementation(Libs.COMPOSE_MATERIAL)

    implementation(Libs.KOTLIN_COROUTINES)
    implementation(Libs.LIFECYCLE_RUNTIME_KTX)
    implementation(Libs.LIFECYCLE_VIEWMODEL_KTX)
    implementation(Libs.FRAGMENT_KTX)

    implementation(Libs.DAGGER)
    kapt(Libs.DAGGER_COMPILER)
}

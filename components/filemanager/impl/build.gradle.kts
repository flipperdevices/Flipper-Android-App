plugins {
    androidLibrary
    id("com.squareup.anvil")
    kotlin("kapt")
}

dependencies {
    implementation(project(":components:core:ui"))
    implementation(project(":components:core:di"))
    implementation(project(":components:core:log"))
    implementation(project(":components:core:ktx"))
    implementation(project(":components:core:navigation"))

    implementation(project(":components:bridge:service:api"))
    implementation(project(":components:bridge:api"))
    implementation(project(":components:bridge:dao"))
    implementation(project(":components:bridge:protobuf"))

    implementation(project(":components:filemanager:api"))

    implementation(project(":components:deeplink:api"))

    implementation(project(":components:share:api"))

    implementation(project(":components:bottombar:api"))

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

    implementation(Libs.CICERONE)
}

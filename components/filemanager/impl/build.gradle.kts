plugins {
    androidCompose
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
    implementation(projects.components.bridge.dao.api)
    implementation(project(":components:bridge:protobuf"))

    implementation(project(":components:filemanager:api"))

    implementation(project(":components:deeplink:api"))

    implementation(project(":components:share:api"))

    implementation(project(":components:bottombar:api"))

    implementation(Libs.ANNOTATIONS)
    implementation(Libs.APPCOMPAT)

    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)

    implementation(Libs.KOTLIN_COROUTINES)
    implementation(Libs.LIFECYCLE_RUNTIME_KTX)
    implementation(Libs.LIFECYCLE_VIEWMODEL_KTX)
    implementation(Libs.FRAGMENT_KTX)

    implementation(libs.dagger)
    kapt(libs.dagger.kapt)

    implementation(Libs.CICERONE)
}

plugins {
    androidCompose
    id("com.squareup.anvil")
    kotlin("kapt")
}

android {
    defaultConfig {
        val shareFileAuthorities = "com.flipperdevices.share.impl.provider"
        manifestPlaceholders["shareFileAuthorities"] = shareFileAuthorities
        buildConfigField("String", "SHARE_FILE_AUTHORITIES", "\"$shareFileAuthorities\"")
    }
}

dependencies {
    implementation(project(":components:core:di"))
    implementation(project(":components:core:ktx"))
    implementation(project(":components:core:ui"))
    implementation(project(":components:core:log"))

    implementation(project(":components:share:api"))
    implementation(project(":components:share:common"))

    implementation(project(":components:bridge:api"))
    implementation(project(":components:bridge:protobuf"))
    implementation(project(":components:bridge:service:api"))

    implementation(Libs.ANNOTATIONS)
    implementation(Libs.APPCOMPAT)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)

    // Dagger deps
    implementation(Libs.DAGGER)
    kapt(Libs.DAGGER_COMPILER)

    // Lifecycle
    implementation(Libs.LIFECYCLE_RUNTIME_KTX)
    implementation(Libs.LIFECYCLE_VIEWMODEL_KTX)
    implementation(Libs.LIFECYCLE_COMPOSE)
}

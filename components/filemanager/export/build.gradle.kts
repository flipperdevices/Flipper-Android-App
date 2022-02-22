plugins {
    androidCompose
    id("com.squareup.anvil")
    kotlin("kapt")
}

android {
    buildTypes {
        debug {
            val applicationId = ApkConfig.APPLICATION_ID + ApkConfig.APPLICATION_ID_SUFFIX
            val shareFileAuthorities = "$applicationId.filemanager.export.provider"
            manifestPlaceholders["shareFileAuthorities"] = shareFileAuthorities
            buildConfigField("String", "SHARE_FILE_AUTHORITIES", "\"$shareFileAuthorities\"")
        }
        internal {
            val applicationId = ApkConfig.APPLICATION_ID
            val shareFileAuthorities = "$applicationId.filemanager.export.provider"
            manifestPlaceholders["shareFileAuthorities"] = shareFileAuthorities
            buildConfigField("String", "SHARE_FILE_AUTHORITIES", "\"$shareFileAuthorities\"")
        }
        release {
            val applicationId = ApkConfig.APPLICATION_ID
            val shareFileAuthorities = "$applicationId.filemanager.export.provider"
            manifestPlaceholders["shareFileAuthorities"] = shareFileAuthorities
            buildConfigField("String", "SHARE_FILE_AUTHORITIES", "\"$shareFileAuthorities\"")
        }
    }
}

dependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.ui)
    implementation(projects.components.core.log)

    implementation(projects.components.filemanager.api)
    implementation(projects.components.filemanager.sharecommon)

    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.protobuf)
    implementation(projects.components.bridge.service.api)

    implementation(libs.annotations)
    implementation(libs.appcompat)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)

    // Lifecycle
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.compose)
}

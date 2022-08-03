import com.flipperdevices.buildlogic.plugins.ApkConfig

plugins {
    id("androidCompose")
    id("com.squareup.anvil")
    kotlin("kapt")
    id("kotlin-parcelize")
    id("kotlinx-serialization")
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
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.navigation)
    implementation(projects.components.core.ui.fragment)
    implementation(projects.components.core.ui.navigation)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.core.ui.res)

    implementation(projects.components.bridge.service.api)
    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.pbutils)
    implementation(libs.protobuf.jvm)

    implementation(projects.components.filemanager.api)

    implementation(libs.kotlin.serialization.json)

    implementation(projects.components.deeplink.api)

    implementation(projects.components.bottombar.api)

    implementation(libs.annotations)
    implementation(libs.appcompat)

    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.navigation)

    implementation(libs.kotlin.coroutines)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.ktx.fragment)

    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
    implementation(libs.tangle.viewmodel.compose)
    implementation(libs.tangle.viewmodel.api)
    anvil(libs.tangle.viewmodel.compiler)

    implementation(libs.cicerone)
}

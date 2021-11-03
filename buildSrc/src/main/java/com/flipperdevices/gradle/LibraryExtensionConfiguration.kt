package com.flipperdevices.gradle

import ApkConfig
import com.android.build.gradle.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project

fun LibraryExtension.configure(project: Project) {
    configureDefaultConfig()
    configureBuildTypes()
    configureBuildFeatures()
    configureCompileOptions()
}

private fun LibraryExtension.configureDefaultConfig() {
    compileSdk = ApkConfig.COMPILE_SDK_VERSION
    defaultConfig.minSdk = ApkConfig.MIN_SDK_VERSION
    defaultConfig.targetSdk = ApkConfig.TARGET_SDK_VERSION
    defaultConfig.consumerProguardFiles("consumer-rules.pro")
    defaultConfig.versionCode = ApkConfig.VERSION_CODE
    defaultConfig.versionName = ApkConfig.VERSION_NAME
}

private fun LibraryExtension.configureBuildTypes() {
    buildTypes { container ->
        container.maybeCreate("debug").apply {
            buildConfigField("boolean", "INTERNAL", "true")
        }
        container.maybeCreate("internal").apply {
            setMatchingFallbacks("debug")
            sourceSets.getByName(this.name).setRoot("src/debug")

            isMinifyEnabled = true

            buildConfigField("boolean", "INTERNAL", "true")
            consumerProguardFiles(
                "proguard-rules.pro"
            )
        }
        container.maybeCreate("release").apply {
            isMinifyEnabled = true

            buildConfigField("boolean", "INTERNAL", "false")
            consumerProguardFiles(
                "proguard-rules.pro"
            )
        }
    }
}

private fun LibraryExtension.configureBuildFeatures() {
    buildFeatures.viewBinding = true
}

private fun LibraryExtension.configureCompileOptions() {
    compileOptions.sourceCompatibility = JavaVersion.VERSION_1_8
    compileOptions.targetCompatibility = JavaVersion.VERSION_1_8
}

package com.flipper.gradle

import ApkConfig
import com.android.build.gradle.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project

fun LibraryExtension.configure(project: Project) {
    configureDefaultConfig()
    configureBuildTypes()
    configureBuildFeatures()
    configureCompileOptions()
    configureComposeOptions()
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
            buildConfigField("boolean", "INTERNAL", "true")
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        container.maybeCreate("release").apply {
            buildConfigField("boolean", "INTERNAL", "false")
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

private fun LibraryExtension.configureBuildFeatures() {
    buildFeatures.viewBinding = true
    buildFeatures.compose = true
}

private fun LibraryExtension.configureCompileOptions() {
    compileOptions.sourceCompatibility = JavaVersion.VERSION_1_8
    compileOptions.targetCompatibility = JavaVersion.VERSION_1_8
}

private fun LibraryExtension.configureComposeOptions() {
    composeOptions.kotlinCompilerExtensionVersion = Versions.ANDROID_JETPACK_COMPOSE
}
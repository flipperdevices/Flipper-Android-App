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
}

private fun LibraryExtension.configureDefaultConfig() {
    compileSdkVersion(ApkConfig.COMPILE_SDK_VERSION)
    defaultConfig.setMinSdkVersion(ApkConfig.MIN_SDK_VERSION)
    defaultConfig.setTargetSdkVersion(ApkConfig.TARGET_SDK_VERSION)
    defaultConfig.consumerProguardFiles("consumer-rules.pro")
    defaultConfig.setVersionCode(ApkConfig.VERSION_CODE)
    defaultConfig.setVersionName(ApkConfig.VERSION_NAME)
}

private fun LibraryExtension.configureBuildTypes() {
    buildTypes { container ->
        container.maybeCreate("debug")
        container.maybeCreate("internal").apply {
            isMinifyEnabled = true
            isDebuggable = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        container.maybeCreate("release").apply {
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
}

private fun LibraryExtension.configureCompileOptions() {
    compileOptions.sourceCompatibility = JavaVersion.VERSION_1_8
    compileOptions.targetCompatibility = JavaVersion.VERSION_1_8
}

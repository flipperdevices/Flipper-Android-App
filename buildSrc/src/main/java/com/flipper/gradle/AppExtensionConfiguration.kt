package com.flipper.gradle

import ApkConfig
import com.android.build.gradle.AppExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project

fun AppExtension.configure(project: Project) {
    configureDefaultConfig()
    configureBuildTypes()
    configureBuildFeatures()
    configureCompileOptions()
}

private fun AppExtension.configureDefaultConfig() {
    compileSdkVersion(ApkConfig.COMPILE_SDK_VERSION)
    defaultConfig.setApplicationId(ApkConfig.APPLICATION_ID)
    defaultConfig.setMinSdkVersion(ApkConfig.MIN_SDK_VERSION)
    defaultConfig.setTargetSdkVersion(ApkConfig.TARGET_SDK_VERSION)
    defaultConfig.consumerProguardFiles("consumer-rules.pro")
    defaultConfig.setVersionCode(ApkConfig.VERSION_CODE)
    defaultConfig.setVersionName(ApkConfig.VERSION_NAME)
}

private fun AppExtension.configureBuildTypes() {
    buildTypes { container ->
        container.maybeCreate("debug").apply {
            applicationIdSuffix = ".dev"
            multiDexEnabled = true
            isDebuggable = true
        }
        container.maybeCreate("internal").apply {
            applicationIdSuffix = ".internal"
            //isDebuggable = true
            isMinifyEnabled = true
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

private fun AppExtension.configureBuildFeatures() {
    buildFeatures.viewBinding = true
}

private fun AppExtension.configureCompileOptions() {
    compileOptions.sourceCompatibility = JavaVersion.VERSION_1_8
    compileOptions.targetCompatibility = JavaVersion.VERSION_1_8
}

package com.flipperdevices.gradle

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
    defaultConfig.applicationId = ApkConfig.APPLICATION_ID
    defaultConfig.minSdk = ApkConfig.MIN_SDK_VERSION
    defaultConfig.targetSdk = ApkConfig.TARGET_SDK_VERSION
    defaultConfig.consumerProguardFiles("consumer-rules.pro")
    defaultConfig.versionCode = ApkConfig.VERSION_CODE
    defaultConfig.versionName = ApkConfig.VERSION_NAME
}

private fun AppExtension.configureBuildTypes() {
    buildTypes { container ->
        defaultConfig {
            it.manifestPlaceholders[SPLASH_SCREEN_ACTIVITY_KEY] = SPLASH_SCREEN_ACTIVITY
        }
        container.maybeCreate("debug").apply {
            buildConfigField("boolean", "INTERNAL", "true")
            applicationIdSuffix = ".dev"
            multiDexEnabled = true
            isDebuggable = true
        }
        container.maybeCreate("internal").apply {
            setMatchingFallbacks("debug")
            sourceSets.getByName(this.name).setRoot("src/debug")

            buildConfigField("boolean", "INTERNAL", "true")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        container.maybeCreate("release").apply {
            buildConfigField("boolean", "INTERNAL", "false")
            isMinifyEnabled = true
            isShrinkResources = true
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

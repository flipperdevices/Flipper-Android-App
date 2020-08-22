package com.flipper.gradle

import ApkConfig
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project

fun LibraryExtension.configure(project: Project) {
  configureDefaultConfig()
  configureBuildTypes()
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
    container.maybeCreate("release").apply {
      isMinifyEnabled = true
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
      )
    }
  }
}

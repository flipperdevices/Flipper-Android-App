package com.flipper.gradle

import Versions
import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class ComposerPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.all { plugin ->
            when (plugin) {
                is LibraryPlugin -> {
                    project.extensions.getByType(LibraryExtension::class.java).configure()
                }
                is AppPlugin -> {
                    project.extensions.getByType(AppExtension::class.java).configure()
                }
            }
        }
    }
}

private fun AppExtension.configure() {
    enableComposeBuildFeature()
    configureComposeOptions()
}

private fun AppExtension.enableComposeBuildFeature() {
    buildFeatures.compose = true
}

private fun AppExtension.configureComposeOptions() {
    composeOptions.kotlinCompilerExtensionVersion = Versions.ANDROID_JETPACK_COMPOSE
}

private fun LibraryExtension.configure() {
    enableComposeBuildFeature()
    configureComposeOptions()
}

private fun LibraryExtension.enableComposeBuildFeature() {
    buildFeatures.compose = true
}

private fun LibraryExtension.configureComposeOptions() {
    composeOptions.kotlinCompilerExtensionVersion = Versions.ANDROID_JETPACK_COMPOSE
}
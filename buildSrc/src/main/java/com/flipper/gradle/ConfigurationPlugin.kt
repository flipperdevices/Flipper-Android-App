package com.flipper.gradle

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class ConfigurationPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.all { plugin ->
            when (plugin) {
                is LibraryPlugin -> {
                    project.extensions.getByType(LibraryExtension::class.java).configure(project)
                }
                is AppPlugin -> {
                    project.extensions.getByType(AppExtension::class.java).configure(project)
                }
            }
        }
        project.pluginManager.apply("com.squareup.anvil")
        if (!project.pluginManager.hasPlugin("kotlin-android")) {
            project.pluginManager.apply("kotlin-android")
        }
    }
}

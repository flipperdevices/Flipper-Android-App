package com.flipperdevices.buildlogic.plugin

import com.flipperdevices.buildlogic.util.ProjectExt.kotlin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

class FlipperMultiplatformDependenciesPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.kotlin.sourceSets.forEach { sourceSet ->
            val fullSourceSetName = sourceSet.name
            val noMainSourceSetName = if (fullSourceSetName.endsWith(MAIN_SOURCE_SET_POSTFIX)) {
                fullSourceSetName.replace(
                    oldValue = MAIN_SOURCE_SET_POSTFIX,
                    newValue = ""
                )
            } else {
                fullSourceSetName
            }

            target.extensions.create(
                "${noMainSourceSetName}Dependencies",
                FlipperMultiplatformDependenciesScope::class,
                target,
                fullSourceSetName
            )
        }
    }

    companion object {
        /**
         * The postfix of default source set naming aka commonMain, androidMain etc
         */
        private const val MAIN_SOURCE_SET_POSTFIX = "Main"
    }
}

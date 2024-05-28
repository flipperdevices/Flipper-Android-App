package com.flipperdevices.buildlogic.plugin

import com.flipperdevices.buildlogic.util.ProjectExt.kotlin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import org.jetbrains.kotlin.gradle.plugin.mpp.DefaultKotlinDependencyHandler

open class FlipperMultiplatformDependenciesScope(
    project: Project,
    name: String
) : KotlinDependencyHandler by DefaultKotlinDependencyHandler(
    parent = project.kotlin.sourceSets.getByName(name),
    project = project
)

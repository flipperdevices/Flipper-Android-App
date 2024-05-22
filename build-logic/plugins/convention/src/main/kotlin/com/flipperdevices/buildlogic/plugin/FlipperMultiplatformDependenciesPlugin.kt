package com.flipperdevices.buildlogic.plugin

import gradle.kotlin.dsl.accessors._089327967f8e17bf02e7a1b7ad8a66b0.kotlin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

class FlipperMultiplatformDependenciesPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.kotlin.sourceSets.forEach {
            target.extensions.create(
                "${it.name}Dependencies",
                FlipperMultiplatformDependenciesScope::class,
                target,
                it.name
            )
        }
    }
}

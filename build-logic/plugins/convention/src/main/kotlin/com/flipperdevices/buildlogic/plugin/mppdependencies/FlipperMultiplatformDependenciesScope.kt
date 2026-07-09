package com.flipperdevices.buildlogic.plugin.mppdependencies

import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

open class FlipperMultiplatformDependenciesScope(
    handler: KotlinDependencyHandler
) : KotlinDependencyHandler by handler

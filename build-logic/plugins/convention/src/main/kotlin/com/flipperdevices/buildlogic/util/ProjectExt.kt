package com.flipperdevices.buildlogic.util

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal object ProjectExt {
    /**
     * Required to remove gradle.kotlin.dsl.accessors dependency
     */
    val Project.kotlin: KotlinMultiplatformExtension
        get() {
            val extensionAware = (this as org.gradle.api.plugins.ExtensionAware)
            val kotlinExtension = extensionAware.extensions.getByName("kotlin")
            return kotlinExtension as KotlinMultiplatformExtension
        }
}

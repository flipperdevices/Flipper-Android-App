package com.flipperdevices.buildlogic.plugin.composefiles

import com.flipperdevices.buildlogic.plugin.composefiles.FileAccessorsGenerator.asUnderscoredIdentifierIfNeeded
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.resources.ResourcesExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class ComposeFileAccessorsPlugin : Plugin<Project> {

    private fun Project.deriveResourcePackage(): String {
        val groupName = group.toString().lowercase().asUnderscoredIdentifierIfNeeded()
        val moduleName = name.lowercase().asUnderscoredIdentifierIfNeeded()
        return if (groupName.isNotEmpty()) {
            "$groupName.$moduleName.generated.resources"
        } else {
            "$moduleName.generated.resources"
        }
    }

    override fun apply(project: Project) {
        val generateTask = project
            .tasks
            .register<GenerateComposeFileAccessorsTask>("generateComposeFileAccessors") {
                outputDir.set(project.layout.buildDirectory.dir("generated/composeFileAccessors/commonMain/kotlin"))
                packageName.set(project.deriveResourcePackage())
                resClassName.set("Res")
                makeAccessorsPublic.set(false)
            }

        project.plugins.withId("org.jetbrains.kotlin.multiplatform") {
            project.extensions.getByType<KotlinMultiplatformExtension>()
                .sourceSets
                .named("commonMain")
                .configure {
                    kotlin.srcDir(generateTask.flatMap { task -> task.outputDir })
                }
        }

        project.afterEvaluate {
            val resourcesExtension = project.extensions
                .findByType<ComposeExtension>()
                ?.extensions?.findByType<ResourcesExtension>()

            val composeResourcesDir = project.layout
                .projectDirectory
                .dir("src/commonMain/composeResources")

            generateTask.configure {
                inputDir.set(composeResourcesDir.dir("files"))
                resClassName.set(
                    resourcesExtension?.nameOfResClass
                        ?.takeIf { resClassName -> resClassName.isNotBlank() }
                        ?: "Res"
                )
                makeAccessorsPublic.set(resourcesExtension?.publicResClass ?: false)
            }
        }
    }
}

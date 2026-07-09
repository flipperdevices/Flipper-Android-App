package com.flipperdevices.buildlogic.plugin.composefiles

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class GenerateComposeFileAccessorsTask : DefaultTask() {

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    @get:Optional
    abstract val inputDir: DirectoryProperty

    @get:Input
    abstract val resClassName: Property<String>

    @get:Input
    abstract val packageName: Property<String>

    @get:Input
    abstract val makeAccessorsPublic: Property<Boolean>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val outDir = outputDir.get().asFile
        outDir.deleteRecursively()
        outDir.mkdirs()

        if (!inputDir.isPresent) return
        val srcDir = inputDir.get().asFile
        if (!srcDir.isDirectory) return

        val fileNames = srcDir.listFiles()
            ?.filter { file -> file.isFile }
            ?.filter { file -> !file.name.startsWith(".") }
            ?.sortedBy { file -> file.name }
            ?.map { file -> file.name }
            ?: return

        if (fileNames.isEmpty()) return

        FileAccessorsGenerator.generateFileAccessors(
            fileNames = fileNames,
            dirName = srcDir.name,
            packageName = packageName.get(),
            resClassName = resClassName.get(),
            makeAccessorsPublic = makeAccessorsPublic.get(),
            outputDir = outDir,
        )
    }
}

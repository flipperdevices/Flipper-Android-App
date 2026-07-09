package com.flipperdevices.buildlogic.plugin.composefiles

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import java.io.File

internal object FileAccessorsGenerator {

    /**
     * For files, starting with numbers, we need to add underscore, so it would be
     * available in kotlin code
     */
    fun String.asUnderscoredIdentifierIfNeeded(): String {
        return replace('-', '_')
            .let { string -> if (string.firstOrNull()?.isDigit() == true) "_$string" else string }
    }

    fun generateFileAccessors(
        fileNames: List<String>,
        dirName: String,
        packageName: String,
        resClassName: String,
        makeAccessorsPublic: Boolean,
        outputDir: File,
    ) {
        val visibilityModifier = if (makeAccessorsPublic) KModifier.PUBLIC else KModifier.INTERNAL
        val resType = ClassName(packageName, resClassName)
        val accessorClass = ClassName(packageName, "${resClassName}FileAccessor")
        val accessorObject = ClassName(
            packageName,
            "$resClassName${dirName.replaceFirstChar { char -> char.uppercase() }}Accessor"
        )

        val accessorClassSpec = TypeSpec.classBuilder(accessorClass)
            .addModifiers(visibilityModifier)
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addModifiers(KModifier.INTERNAL)
                    .addParameter(ParameterSpec.builder("path", String::class).build())
                    .build()
            )
            .addProperty(
                PropertySpec.builder("path", String::class, KModifier.PUBLIC)
                    .initializer("path")
                    .build()
            )
            .addFunction(
                FunSpec.builder("readBytes")
                    .addModifiers(KModifier.SUSPEND)
                    .returns(ByteArray::class)
                    .addStatement("return %T.readBytes(path)", resType)
                    .build()
            )
            .addFunction(
                FunSpec.builder("getUri")
                    .returns(String::class)
                    .addStatement("return %T.getUri(path)", resType)
                    .build()
            )
            .build()

        val extensionProp = PropertySpec.builder(dirName, accessorObject)
            .addModifiers(visibilityModifier)
            .receiver(resType)
            .getter(FunSpec.getterBuilder().addStatement("return %T", accessorObject).build())
            .build()

        val accessorObjectSpec = TypeSpec.objectBuilder(accessorObject)
            .addModifiers(visibilityModifier)
            .apply {
                fileNames.forEach { name ->
                    val propName = name.substringBeforeLast('.').asUnderscoredIdentifierIfNeeded()
                    addProperty(
                        PropertySpec.builder(propName, accessorClass)
                            .initializer("%T(%S)", accessorClass, "$dirName/$name")
                            .build()
                    )
                }
            }
            .build()

        FileSpec.builder(packageName, "${dirName.replaceFirstChar { char -> char.uppercase() }}Ext")
            .addType(accessorClassSpec)
            .addProperty(extensionProp)
            .addType(accessorObjectSpec)
            .build()
            .writeTo(outputDir)
    }
}

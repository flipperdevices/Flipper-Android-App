package com.flipperdevices.buildlogic.property

import org.gradle.api.GradleException
import org.gradle.api.Project
import java.util.Properties

class SecretPropertyValue(
    private val project: Project,
    override val key: String
) : PropertyValue {
    override fun getValue(): Result<String> {
        val envValue = System.getenv(key)?.toString()
        if (envValue != null) {
            return Result.success(envValue)
        } else {
            project.logger.error("Key $key is not found in Environment. Getting from local.properties")
        }
        val secretPropsFile = project.file("local.properties")
        if (!secretPropsFile.exists()) {
            val e = IllegalStateException("File ${secretPropsFile.name} doesn't exist")
            return Result.failure(e)
        }
        val properties = Properties().apply {
            load(secretPropsFile.reader())
        }
        return runCatching {
            properties[key]
                ?.toString()
                ?: throw GradleException("Required property $key not defined!")
        }
    }
}

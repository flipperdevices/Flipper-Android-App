@file:Suppress("Filename")

import org.gradle.api.Project

fun Project.includeCommonKspConfigurationTo(
    vararg toConfigurations: String,
) {
    pluginManager.withPlugin("com.google.devtools.ksp") {
        val commonKsp = configurations.create("commonKsp")
        toConfigurations.forEach { configurationName ->
            configurations.getByName(configurationName).extendsFrom(commonKsp)
        }
    }
}

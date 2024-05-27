plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    alias(libs.plugins.buildkonfig)
}

group = "com.flipperdevices.core.buildkonfig"

android.namespace = "$group"

commonDependencies {
    implementation(libs.timber)
}

buildConfig {
    className("BuildKonfig")
    packageName("$group")
    useKotlinOutput { internalVisibility = false }
    buildConfigField(String::class.java, "PACKAGE", "$group")
    "IS_LOG_ENABLED".let { key ->
        buildConfigField(
            type = Boolean::class.java,
            name = key,
            value = provider {
                val isLogEnabled = providers.gradleProperty(key).orNull
                if (isLogEnabled == null) {
                    logger.warn("Key $key is not present in gradle.properties or Environment")
                }
                isLogEnabled ?: true
            }
        )
    }
}

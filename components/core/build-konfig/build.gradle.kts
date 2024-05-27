import com.github.gmazzo.buildconfig.BuildConfigExtension

plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    alias(libs.plugins.buildkonfig)
}

group = "com.flipperdevices.core.buildkonfig"

android.namespace = "$group"

/**
 * This value can be placed inside gradle.properties `IS_LOG_ENABLED=false` or passed
 * via ci as ORG_GRADLE_PROJECT_IS_LOG_ENABLED
 */
fun BuildConfigExtension.buildConfigIsLogEnabledField() {
    val key = "IS_LOG_ENABLED"
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

buildConfig {
    className("BuildKonfig")
    packageName("$group")
    useKotlinOutput { internalVisibility = false }
    buildConfigField(String::class.java, "PACKAGE", "$group")
    buildConfigIsLogEnabledField()
}

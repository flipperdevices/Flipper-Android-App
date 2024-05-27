import com.flipperdevices.buildlogic.ApkConfig.CURRENT_FLAVOR_TYPE
import com.flipperdevices.buildlogic.model.FlavorType
import com.github.gmazzo.buildconfig.BuildConfigExtension

plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    alias(libs.plugins.buildkonfig)
}

group = "com.flipperdevices.core.buildkonfig"

android.namespace = "$group"

fun BuildConfigExtension.buildConfigIsLogEnabledField() {
    val key = "IS_LOG_ENABLED"
    buildConfigField(
        type = Boolean::class.java,
        name = key,
        value = provider { CURRENT_FLAVOR_TYPE == FlavorType.DEV }
    )
}

buildConfig {
    className("BuildKonfig")
    packageName("$group")
    useKotlinOutput { internalVisibility = false }
    buildConfigField(String::class.java, "PACKAGE", "$group")
    buildConfigIsLogEnabledField()
}

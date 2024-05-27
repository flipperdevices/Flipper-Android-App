import com.flipperdevices.buildlogic.ApkConfig.CURRENT_FLAVOR_TYPE

plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    alias(libs.plugins.buildkonfig)
}

group = "com.flipperdevices.core.buildkonfig"

android.namespace = "$group"

buildConfig {
    className("BuildKonfig")
    packageName("$group")
    useKotlinOutput { internalVisibility = false }
    buildConfigField(String::class.java, "PACKAGE", "$group")
    buildConfigField(Boolean::class.java, "IS_LOG_ENABLED", CURRENT_FLAVOR_TYPE.isLogEnabled)
}

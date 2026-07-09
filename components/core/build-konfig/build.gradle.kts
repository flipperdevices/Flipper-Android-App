import com.flipperdevices.buildlogic.ApkConfig
import com.flipperdevices.buildlogic.ApkConfig.COUNTLY_APP_KEY
import com.flipperdevices.buildlogic.ApkConfig.COUNTLY_URL
import com.flipperdevices.buildlogic.ApkConfig.CURRENT_FLAVOR_TYPE
import com.flipperdevices.buildlogic.ApkConfig.IS_GOOGLE_FEATURE_AVAILABLE
import com.flipperdevices.buildlogic.ApkConfig.TARGET_APP_ID_KEY
import dev.detekt.gradle.Detekt
import org.gradle.kotlin.dsl.buildConfigField
import org.gradle.kotlin.dsl.withType

plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    alias(libs.plugins.buildkonfig)
}

group = "com.flipperdevices.core.buildkonfig"

buildConfig {
    className("BuildKonfig")
    packageName("$group")
    useKotlinOutput { internalVisibility = false }
    buildConfigField(String::class.java, "PACKAGE", "$group")
    buildConfigField(Boolean::class.java, "IS_LOG_ENABLED", CURRENT_FLAVOR_TYPE.isLogEnabled)
    buildConfigField(
        Boolean::class.java,
        "IS_VERBOSE_LOG_ENABLED",
        CURRENT_FLAVOR_TYPE.isVerboseLogEnabled
    )
    buildConfigField(String::class.java, "TARGET_APP_ID_KEY", TARGET_APP_ID_KEY)
    buildConfigField(String::class.java, "COUNTLY_URL", COUNTLY_URL)
    buildConfigField(String::class.java, "COUNTLY_APP_KEY", COUNTLY_APP_KEY)
    buildConfigField(
        Boolean::class.java,
        "IS_GOOGLE_FEATURE_AVAILABLE",
        project.IS_GOOGLE_FEATURE_AVAILABLE
    )
    buildConfigField(
        Boolean::class.java,
        "CRASH_APP_ON_FAILED_CHECKS",
        CURRENT_FLAVOR_TYPE.crashAppOnFailedChecks
    )
    // Must match the ${applicationId}-based authority declared in src/androidMain/AndroidManifest.xml.
    buildConfigField(
        String::class.java,
        "SHARE_FILE_AUTHORITIES",
        "${ApkConfig.APPLICATION_ID}.filemanager.export.provider"
    )
    buildConfigField(
        Boolean::class.java,
        "LOGGING_PENDING_COMMANDS",
        CURRENT_FLAVOR_TYPE.loggingPendingCommands
    )
    buildConfigField(
        Integer::class.java,
        "ROBOELECTRIC_SDK_VERSION",
        ApkConfig.ROBOELECTRIC_SDK_VERSION
    )
}

tasks.withType<Detekt> {
    enabled = false
}

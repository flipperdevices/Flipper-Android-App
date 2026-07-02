import com.android.build.api.dsl.ApplicationExtension
import com.flipperdevices.buildlogic.ApkConfig
import com.flipperdevices.buildlogic.ApkConfig.IS_SENTRY_PUBLISH
import io.sentry.android.gradle.extensions.SentryPluginExtension

plugins {
    id("com.android.application")
    id("io.sentry.android.gradle")
    id("flipper.lint")
}

pluginManager.apply("org.jetbrains.kotlin.android")

@Suppress("UnstableApiUsage")
configure<ApplicationExtension> {
    commonAndroid(project)
    sourceSets.getByName("main").setRoot("src/androidMain")

    defaultConfig {
        applicationId = ApkConfig.APPLICATION_ID
    }

    buildTypes {
        internal {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        release {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

includeCommonKspConfigurationTo("ksp")

configure<SentryPluginExtension> {
    autoUploadProguardMapping.set(IS_SENTRY_PUBLISH)
    telemetry.set(false)

    ignoredBuildTypes.set(setOf("release", "debug"))

    autoInstallation.enabled.set(false)
}

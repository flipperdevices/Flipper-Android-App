import com.android.build.api.dsl.ApplicationExtension
import com.android.build.gradle.BaseExtension
import com.flipperdevices.buildlogic.ApkConfig
import com.flipperdevices.buildlogic.ApkConfig.IS_SENTRY_PUBLISH
import io.sentry.android.gradle.extensions.SentryPluginExtension

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("io.sentry.android.gradle")
}

@Suppress("UnstableApiUsage")
configure<BaseExtension> {
    commonAndroid(project)

    defaultConfig {
        applicationId = ApkConfig.APPLICATION_ID
    }

    buildTypes {
        internal {
            isShrinkResources = true
        }
        release {
            isShrinkResources = true
        }
    }
}

configure<ApplicationExtension> {
    // https://issuetracker.google.com/issues/162074215
    dependenciesInfo {
        includeInBundle = false
        includeInApk = false
    }
}

configure<SentryPluginExtension> {
    autoUploadProguardMapping.set(IS_SENTRY_PUBLISH)

    ignoredBuildTypes.set(setOf("release", "debug"))

    autoInstallation.enabled.set(false)
}

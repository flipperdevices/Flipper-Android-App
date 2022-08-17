import com.android.build.gradle.BaseExtension
import com.flipperdevices.buildlogic.ApkConfig
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
        debug {
            applicationIdSuffix = ApkConfig.APPLICATION_ID_SUFFIX
        }
        internal {
            isShrinkResources = true
        }
        release {
            isShrinkResources = true
        }
    }
}

configure<SentryPluginExtension> {
    autoUploadProguardMapping.set(ApkConfig.IS_SENTRY_PUBLISH)

    ignoredBuildTypes.set(setOf("release", "debug"))
}

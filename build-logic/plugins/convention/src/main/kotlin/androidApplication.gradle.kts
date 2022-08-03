import com.flipperdevices.buildlogic.plugins.ApkConfig

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("io.sentry.android.gradle")
    id("flipper.apk-config")
}

@Suppress("UnstableApiUsage")
android {
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

sentry {
    autoUploadProguardMapping.set(ApkConfig.IS_SENTRY_PUBLISH)

    ignoredBuildTypes.set(setOf("release", "debug"))
}

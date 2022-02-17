plugins {
    id("com.android.application")
    id("kotlin-android")
    id("io.sentry.android.gradle")
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

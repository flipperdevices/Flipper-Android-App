plugins {
    id("com.android.application")
    id("kotlin-android")
}

@Suppress("UnstableApiUsage")
android {
    commonAndroid(project)

    buildTypes {
        debug {
            applicationIdSuffix = ".dev"
        }
        maybeCreate("internal").apply {
            isShrinkResources = true
        }
        release {
            isShrinkResources = true
        }
    }
}

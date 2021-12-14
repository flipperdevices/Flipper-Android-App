plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    commonAndroid(project)
    common()

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

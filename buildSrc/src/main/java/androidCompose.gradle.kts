plugins {
    id("androidLibrary")
}

@Suppress("UnstableApiUsage")
android {
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.ANDROID_JETPACK_COMPOSE
    }
}

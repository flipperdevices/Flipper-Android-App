plugins {
    id("androidLibrary")
}

@Suppress("UnstableApiUsage")
android {
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.1.0-beta04"
    }
}

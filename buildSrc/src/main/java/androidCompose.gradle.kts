plugins {
    id("androidLibrary")
}

@Suppress("UnstableApiUsage")
android {
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = resolveVersion("compose_compiler")
    }
}

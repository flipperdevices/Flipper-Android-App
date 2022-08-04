plugins {
    id("flipper.android-lib")
}

@Suppress("UnstableApiUsage")
android {
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
}

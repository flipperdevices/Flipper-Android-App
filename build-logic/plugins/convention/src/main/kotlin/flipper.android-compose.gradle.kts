import com.android.build.gradle.BaseExtension

plugins {
    id("flipper.android-lib")
    id("org.jetbrains.kotlin.plugin.compose")
}

@Suppress("UnstableApiUsage")
configure<BaseExtension> {
    buildFeatures.compose = true
}

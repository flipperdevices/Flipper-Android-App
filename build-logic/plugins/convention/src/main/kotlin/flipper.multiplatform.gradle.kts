import com.flipperdevices.buildlogic.ApkConfig
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.kotlin.multiplatform.library")
    id("flipper.lint")
    id("flipper.kotlin-flags")
}

kotlin {
    androidLibrary {
        namespace = "com.flipperdevices"
            .plus(project.path.removePrefix(":components"))
            .replace(":", ".")
            .replace("-", "")
            .replace("..", ".")
        compileSdk = ApkConfig.COMPILE_SDK_VERSION
        minSdk = ApkConfig.MIN_SDK_VERSION
        // Jetbrains Compose Resources doesn't handle the new resourceless android plugin
        androidResources.enable = true

        withHostTest {
            isIncludeAndroidResources = true
        }
    }
    jvm("desktop")

    applyDefaultHierarchyTemplate {
        common {
            group("jvmShared") {
                withCompilations { compilation ->
                    compilation.platformType == KotlinPlatformType.androidJvm ||
                        compilation.platformType == KotlinPlatformType.jvm
                }
            }
        }
    }

    sourceSets.getByName("androidMain").kotlin.srcDir("src/main/kotlin")
    sourceSets.getByName("androidMain").kotlin.srcDir("src/main/java")
    sourceSets.getByName("androidMain").resources.srcDir("src/main/resources")
    sourceSets.getByName("androidHostTest").kotlin.srcDir("src/test/kotlin")
    sourceSets.getByName("androidHostTest").kotlin.srcDir("src/test/java")
    sourceSets.getByName("androidHostTest").resources.srcDir("src/test/resources")
}

project.ignoreNoDiscoveredTests()

includeCommonKspConfigurationTo("kspAndroid", "kspDesktop")

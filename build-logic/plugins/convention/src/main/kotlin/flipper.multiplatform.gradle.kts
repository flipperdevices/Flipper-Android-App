import com.android.build.api.dsl.LibraryExtension
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("flipper.lint")
}

pluginManager.apply("com.android.library")
pluginManager.apply("org.jetbrains.kotlin.multiplatform")

val legacyAndroidManifest = file("src/main/AndroidManifest.xml")

configure<LibraryExtension> {
    commonAndroid(project)

    // Consume the legacy single-target android layout (src/main, src/test) directly, so modules
    // don't have to move sources into src/androidMain. Additive, so existing KMP modules that
    // already use src/androidMain keep working.
    sourceSets.getByName("main") {
        res.srcDir("src/main/res")
        assets.srcDir("src/main/assets")
        if (legacyAndroidManifest.exists()) manifest.srcFile(legacyAndroidManifest)
    }
    sourceSets.getByName("test") {
        resources.srcDir("src/test/resources")
        assets.srcDir("src/test/assets")
    }
}

@OptIn(ExperimentalKotlinGradlePluginApi::class)
configure<KotlinMultiplatformExtension> {
    androidTarget {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_1_8
        }
    }
    jvm("desktop")

    applyDefaultHierarchyTemplate {
        common {
            group("jvmShared") {
                withAndroidTarget()
                withJvm()
            }
        }
    }

    sourceSets.getByName("androidMain").kotlin.srcDir("src/main/kotlin")
    sourceSets.getByName("androidMain").kotlin.srcDir("src/main/java")
    sourceSets.getByName("androidUnitTest").kotlin.srcDir("src/test/kotlin")
    sourceSets.getByName("androidUnitTest").kotlin.srcDir("src/test/java")
}

includeCommonKspConfigurationTo("kspAndroid", "kspDesktop")

import com.android.build.api.dsl.LibraryExtension
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("flipper.lint")
}

pluginManager.apply("com.android.library")
pluginManager.apply("org.jetbrains.kotlin.multiplatform")

configure<LibraryExtension> {
    commonAndroid(project)
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
}

includeCommonKspConfigurationTo("kspAndroid", "kspDesktop")

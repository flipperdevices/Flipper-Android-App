import com.android.build.api.dsl.LibraryExtension

plugins {
    id("com.android.library")
    id("flipper.lint")
}

pluginManager.apply("org.jetbrains.kotlin.android")

configure<LibraryExtension> {
    commonAndroid(project)
}

includeCommonKspConfigurationTo("ksp")

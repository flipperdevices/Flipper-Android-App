import com.android.build.gradle.BaseExtension

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("flipper.lint")
}

configure<BaseExtension> {
    commonAndroid(project)
}

includeCommonKspConfigurationTo("ksp")

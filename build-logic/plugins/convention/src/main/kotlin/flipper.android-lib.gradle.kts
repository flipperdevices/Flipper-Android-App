import com.android.build.gradle.BaseExtension

plugins {
    id("com.android.library")
    id("kotlin-android")
}

configure<BaseExtension> {
    commonAndroid(project)
}

package com.flipperdevices.core.di

import android.app.Activity
import kotlin.reflect.KClass

data class ApplicationParams(
    val startApplicationClass: KClass<out Activity>,
    val version: String,
    val isGooglePlayEnable: Boolean
) {
    companion object {
        fun getIsGooglePlayEnableByProps(): Boolean {
            return System.getProperty("is_google_feature", "true") == "true"
        }
    }
    fun isReleaseBuild(): Boolean {
        return BuildConfig.BUILD_TYPE == "INTERNAL"
    }
}


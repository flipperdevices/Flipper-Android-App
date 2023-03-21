package com.flipperdevices.buildlogic

import java.lang.System.getProperty

object ApkConfig {
    const val APPLICATION_ID = "com.flipperdevices.app"

    const val MIN_SDK_VERSION = 26
    const val TARGET_SDK_VERSION = 34
    const val COMPILE_SDK_VERSION = 33

    val VERSION_CODE = getProperty("version_code", Integer.MAX_VALUE.toString()).toInt()
    val VERSION_NAME = getProperty("version_name", "DEBUG_VERSION")!!
    val COUNTLY_URL = getProperty("countly_url", "https://countly.lionzxy.ru/")!!
    val COUNTLY_APP_KEY = getProperty(
        "countly_app_key",
        "171c41398e2459b068869d6409047680896ed062"
    )!!
    val IS_GOOGLE_FEATURE_AVAILABLE = getProperty("is_google_feature", true.toString()).toBoolean()

    val IS_SENTRY_PUBLISH = getProperty("is_sentry_publish", "false").toBoolean()

    val sourceInstall = when {
        // getProperty("is_github_install", "false").toBoolean() -> SourceInstall.GITHUB
        // getProperty("is_google_play_install", "false").toBoolean() -> SourceInstall.GOOGLE_PLAY
        IS_GOOGLE_FEATURE_AVAILABLE -> SourceInstall.GOOGLE_PLAY
        else -> SourceInstall.NONE
    }
}

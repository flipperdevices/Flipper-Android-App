import java.lang.System.getProperty

object ApkConfig {
    const val APPLICATION_ID = "com.flipperdevices.app"
    const val APPLICATION_ID_SUFFIX = ".dev"

    const val MIN_SDK_VERSION = 26
    const val TARGET_SDK_VERSION = 31
    const val COMPILE_SDK_VERSION = 31

    val VERSION_CODE = getProperty("version_code", Integer.MAX_VALUE.toString()).toInt()
    val VERSION_NAME = getProperty("version_name", "DEBUG_VERSION")!!
    val COUNTLY_URL = getProperty("countly_url", null/*"https://countly.lionzxy.ru/"*/)!!
    val COUNTLY_APP_KEY = getProperty(
        "countly_app_key", null/*"171c41398e2459b068869d6409047680896ed062"*/
    )!!

    val IS_SENTRY_PUBLISH = getProperty("is_sentry_publish", "false").toBoolean()
}

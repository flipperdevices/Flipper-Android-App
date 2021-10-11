import java.lang.System.getProperty

object ApkConfig {
    const val APPLICATION_ID = "com.flipperdevices.app"

    const val MIN_SDK_VERSION = 21
    const val TARGET_SDK_VERSION = 30
    const val COMPILE_SDK_VERSION = 30

    val VERSION_CODE = getProperty("version_code", Integer.MAX_VALUE.toString()).toInt()
    val VERSION_NAME = getProperty("version_name", "DEBUG_VERSION")!!
}

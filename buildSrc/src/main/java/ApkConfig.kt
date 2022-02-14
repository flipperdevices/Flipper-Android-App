import java.lang.System.getProperty

object ApkConfig {
    const val MIN_SDK_VERSION = 21
    const val TARGET_SDK_VERSION = 31
    const val COMPILE_SDK_VERSION = 31

    val VERSION_CODE = getProperty("version_code", Integer.MAX_VALUE.toString()).toInt()
    val VERSION_NAME = getProperty("version_name", "DEBUG_VERSION")!!
}

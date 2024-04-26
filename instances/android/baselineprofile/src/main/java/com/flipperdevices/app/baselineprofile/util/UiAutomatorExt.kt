package com.flipperdevices.app.baselineprofile.util

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import com.flipperdevices.app.baselineprofile.BuildConfig
import org.junit.Assert

object UiAutomatorExt {
    const val MEDIUM_TIMEOUT = 10_000L

    val requirePackageName: String
        get() = InstrumentationRegistry.getArguments()
            .getString(BuildConfig.TARGET_APP_ID_KEY)
            ?: error("targetAppId not passed as instrumentation runner arg")

    /**
     * Grant permission rule doesn't work on some android api levels
     */
    fun UiDevice.grantPermission(permission: String) {
        val command = "pm grant $requirePackageName $permission"
        val output = executeShellCommand(command)
        Assert.assertEquals("", output)
    }

    fun UiDevice.findObjectOrNull(selector: BySelector): UiObject2? {
        if (!hasObject(selector)) return null
        return findObject(selector)
    }

    /**
     * Clear cache and data of current app package
     */
    fun UiDevice.clearCache() {
        val command = "pm clear $requirePackageName"
        val output = executeShellCommand(command)
        Assert.assertTrue(output.contains("Success"))
    }

    /**
     * Awaiting for object and return [findObjectOrNull]
     */
    fun UiDevice.waitForObjectOrNull(
        selector: BySelector,
        timeout: Long = MEDIUM_TIMEOUT
    ): UiObject2? {
        wait(Until.hasObject(selector), timeout)
        return findObjectOrNull(selector)
    }
}

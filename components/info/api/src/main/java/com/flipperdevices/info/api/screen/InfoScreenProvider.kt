package com.flipperdevices.info.api.screen

import com.flipperdevices.deeplink.model.Deeplink
import com.github.terrakok.cicerone.Screen

/**
 * Provide screens for info components
 */
interface InfoScreenProvider {
    fun deviceInformationScreen(deeplink: Deeplink? = null): Screen
}

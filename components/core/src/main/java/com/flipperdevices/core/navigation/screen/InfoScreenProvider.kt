package com.flipperdevices.core.navigation.screen

import com.github.terrakok.cicerone.Screen

/**
 * Provide screens for info components
 */
interface InfoScreenProvider {
    fun deviceInformationScreen(deviceId: String): Screen
}

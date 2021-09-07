package com.flipper.core.navigation.screen

import com.flipper.core.models.BLEDevice
import com.github.terrakok.cicerone.Screen

/**
 * Provide screens for info components
 */
interface InfoScreenProvider {
    fun deviceInformationScreen(device: BLEDevice): Screen
}

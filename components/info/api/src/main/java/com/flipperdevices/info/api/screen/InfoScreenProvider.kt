package com.flipperdevices.info.api.screen

import com.github.terrakok.cicerone.Screen

/**
 * Provide screens for info components
 */
interface InfoScreenProvider {
    fun deviceInformationScreen(): Screen
}

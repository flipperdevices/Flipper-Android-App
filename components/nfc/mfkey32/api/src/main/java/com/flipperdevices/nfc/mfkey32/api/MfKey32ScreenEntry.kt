package com.flipperdevices.nfc.mfkey32.api

import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry

interface MfKey32ScreenEntry : AggregateFeatureEntry {
    fun startDestination(): String
}

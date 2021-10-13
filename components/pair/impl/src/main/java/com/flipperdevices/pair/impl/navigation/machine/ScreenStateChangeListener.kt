package com.flipperdevices.pair.impl.navigation.machine

import com.flipperdevices.pair.impl.navigation.models.PairScreenState

interface ScreenStateChangeListener {
    fun onStateChanged(state: PairScreenState)
}
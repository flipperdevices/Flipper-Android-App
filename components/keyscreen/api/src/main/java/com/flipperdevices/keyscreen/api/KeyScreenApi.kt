package com.flipperdevices.keyscreen.api

import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.github.terrakok.cicerone.Screen

interface KeyScreenApi {
    fun getKeyScreenScreen(keyPath: FlipperKeyPath): Screen
}

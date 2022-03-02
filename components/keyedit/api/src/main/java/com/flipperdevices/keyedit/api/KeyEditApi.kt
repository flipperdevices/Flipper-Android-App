package com.flipperdevices.keyedit.api

import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.github.terrakok.cicerone.Screen

interface KeyEditApi {
    fun getKeyEditScreen(keyPath: FlipperKeyPath): Screen
}

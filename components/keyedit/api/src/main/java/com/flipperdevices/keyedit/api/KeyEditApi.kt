package com.flipperdevices.keyedit.api

import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.github.terrakok.cicerone.Screen

interface KeyEditApi {
    fun getScreen(flipperKeyPath: FlipperKeyPath): Screen
}

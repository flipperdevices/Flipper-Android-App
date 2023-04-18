package com.flipperdevices.keyscreen.api

import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath

interface ChooserKeyScreen {
    fun isSupported(keyPath: FlipperKeyPath): Boolean
    fun getScreen(keyPath: FlipperKeyPath): String
    fun getDeeplink(keyPath: FlipperKeyPath): String?
}

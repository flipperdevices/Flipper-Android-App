package com.flipperdevices.keyscreen.api

import android.content.Intent
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath

interface DeepLinkOpenKey {
    fun getIntentForOpenKey(keyPath: FlipperKeyPath): Intent
}
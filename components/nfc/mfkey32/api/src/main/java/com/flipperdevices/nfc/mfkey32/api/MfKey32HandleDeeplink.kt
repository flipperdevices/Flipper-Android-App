package com.flipperdevices.nfc.mfkey32.api

import android.content.Intent

interface MfKey32HandleDeeplink {
    fun handleDeepLink(intent: Intent)
}

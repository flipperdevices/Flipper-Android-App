package com.flipperdevices.faphub.main.api

import android.content.Intent

interface FapHubHandleDeeplink {
    fun handleDeepLink(intent: Intent)
}

package com.flipperdevices.deeplink.api

import android.content.Context
import android.content.Intent
import com.flipperdevices.deeplink.model.DeepLinkParserDelegatePriority
import com.flipperdevices.deeplink.model.Deeplink

interface DeepLinkParserDelegate {
    fun getPriority(context: Context, intent: Intent): DeepLinkParserDelegatePriority?
    suspend fun fromIntent(context: Context, intent: Intent): Deeplink?
}

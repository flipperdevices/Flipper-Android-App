package com.flipperdevices.share.receive

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info

class ReferrerReceiver : BroadcastReceiver(), LogTagProvider {
    override val TAG: String = "ReferrerReceiver"

    override fun onReceive(context: Context, intent: Intent?) {
        info { "Open broadcast receiver with Intent: $intent" }
    }
}

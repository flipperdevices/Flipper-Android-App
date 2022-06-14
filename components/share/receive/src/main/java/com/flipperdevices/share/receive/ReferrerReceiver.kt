package com.flipperdevices.share.receive

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.flipperdevices.deeplink.api.DeepLinkParser
import com.flipperdevices.singleactivity.api.SingleActivityApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ReferrerReceiver : BroadcastReceiver() {

    @Inject
    lateinit var singleActivityApi: SingleActivityApi

    @Inject
    lateinit var deepLinkParser: DeepLinkParser

    override fun onReceive(context: Context?, intent: Intent?) {
        val uri = intent?.data
        if (uri != null && context != null) {
            GlobalScope.launch {
                val deeplink = deepLinkParser.fromUri(context, uri)
                withContext(Dispatchers.Main) {
                    singleActivityApi.open(deeplink)
                }
            }
        }
    }
}
